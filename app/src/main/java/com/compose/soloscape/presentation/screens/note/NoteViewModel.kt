package com.compose.soloscape.presentation.screens.note

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.compose.soloscape.data.database.ImageToDeleteDao
import com.compose.soloscape.data.database.ImageToUploadDao
import com.compose.soloscape.data.database.entity.ImageToUpload
import com.compose.soloscape.data.repository.MongoDB
import com.compose.soloscape.model.GalleryImage
import com.compose.soloscape.model.GalleryState
import com.compose.soloscape.model.Mood
import com.compose.soloscape.model.Report
import com.compose.soloscape.util.Constants.NOTE_SCREEN_ARG_KEY
import com.compose.soloscape.model.RequestState
import com.compose.soloscape.util.fetchImagesFromFirebase
import com.compose.soloscape.util.toRealmInstant
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import io.realm.kotlin.types.RealmInstant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.mongodb.kbson.ObjectId
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val imagesToUploadDao: ImageToUploadDao,
    private val imageToDeleteDao: ImageToDeleteDao
) : ViewModel() {

    val galleryState = GalleryState()

    var uiState by mutableStateOf(UiState())
        private set

    init {
        getReportIdArgument()
        fetchSelectedReport()
    }

    private fun getReportIdArgument() {
        uiState = uiState.copy(
            selectedReportId = savedStateHandle.get<String>(key = NOTE_SCREEN_ARG_KEY)
        )
    }

    private fun fetchSelectedReport() {
        if (uiState.selectedReportId != null) {
            viewModelScope.launch(Dispatchers.Main) {
                MongoDB.getSelectedNotes(reportId = ObjectId.invoke(uiState.selectedReportId!!))
                    .catch {
                        emit(RequestState.Error(Exception("Report is already deleted.")))
                    }.collect { report ->
                        if (report is RequestState.Success) {
                            setSelectedReport(report = report.data)
                            setTitle(title = report.data.title)
                            setDescription(description = report.data.description)
                            setMood(mood = Mood.valueOf(report.data.mood))

                            fetchImagesFromFirebase(
                                remoteImagePaths = report.data.images,
                                onImageDownload = { downloadedImage ->
                                    galleryState.addImage(
                                        GalleryImage(
                                            image = downloadedImage,
                                            remoteImagePath = extractImagePath(fullImageUrl = downloadedImage.toString())
                                        )
                                    )
                                }
                            )
                        }
                    }
            }
        }
    }

    private fun setSelectedReport(report: Report) {
        uiState = uiState.copy(selectedReport = report)
    }

    fun setTitle(title: String) {
        uiState = uiState.copy(title = title)
    }

    fun setDescription(description: String) {
        uiState = uiState.copy(description = description)
    }

    private fun setMood(mood: Mood) {
        uiState = uiState.copy(mood = mood)
    }

    fun updateDateTime(zonedDateTime: ZonedDateTime) {
        uiState = uiState.copy(updatedDateTime = zonedDateTime.toInstant().toRealmInstant())
    }

    fun insertUpdateNotes(
        report: Report,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.Main) {
            if (uiState.selectedReportId != null) {
                updateNotes(report = report, onSuccess = onSuccess, onError = onError)
            } else {
                insertNotes(report = report, onSuccess = onSuccess, onError = onError)
            }
        }
    }

    private suspend fun insertNotes(
        report: Report,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val result = MongoDB.addNewNotes(report = report.apply {
            if (uiState.updatedDateTime != null) {
                date = uiState.updatedDateTime!!
            }
        })
        if (result is RequestState.Success) {
            uploadImageToFirebase()
            withContext(Dispatchers.Main) {
                onSuccess()
            }
        } else if (result is RequestState.Error) {
            withContext(Dispatchers.Main) {
                onError(result.error.message.toString())
            }
        }
    }

    private suspend fun updateNotes(
        report: Report,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val result = MongoDB.updateNotes(report = report.apply {
            _id = ObjectId.invoke(uiState.selectedReportId!!)
            date =
                if (uiState.updatedDateTime != null) uiState.updatedDateTime!! else uiState.selectedReport!!.date

        })
        if (result is RequestState.Success) {
            uploadImageToFirebase()
            deleteImagesFromFirebase(images = uiState.selectedReport?.images)
            withContext(Dispatchers.Main) {
                onSuccess()
            }
        } else if (result is RequestState.Error) {
            withContext(Dispatchers.Main) {
                onError(result.error.message.toString())
            }
        }
    }

    fun deleteNotes(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            if (uiState.selectedReportId != null) {
                val result = MongoDB.deleteNotes(id = ObjectId.invoke(uiState.selectedReportId!!))
                if (result is RequestState.Success) {
                    withContext(Dispatchers.Main) {
                        uiState.selectedReport?.let { deleteImagesFromFirebase(images = it.images) }
                        onSuccess()
                    }
                } else if (result is RequestState.Error) {
                    withContext(Dispatchers.Main) {
                        onError(result.error.message.toString())
                    }
                }
            }
        }
    }

    fun addImage(image: Uri, imageType: String){
        val remoteImagePath = "images/${FirebaseAuth.getInstance().currentUser?.uid}/" +
                "${image.lastPathSegment}-${System.currentTimeMillis()}.$imageType"

        galleryState.addImage(
            GalleryImage(
                image = image,
                remoteImagePath = remoteImagePath
            )
        )
    }

    private fun uploadImageToFirebase(){
        val storage = FirebaseStorage.getInstance().reference
        galleryState.images.forEach { galleryImage ->
            val imagePath = storage.child(galleryImage.remoteImagePath)
            imagePath.putFile(galleryImage.image)
                .addOnProgressListener {
                    val sessionUri = it.uploadSessionUri
                    if(sessionUri != null){
                        viewModelScope.launch(Dispatchers.IO) {
                            imagesToUploadDao.addImageToUpload(
                                ImageToUpload(
                                    remoteImagePath = galleryImage.remoteImagePath,
                                    imageUri = galleryImage.image.toString(),
                                    sessionUri = sessionUri.toString()
                                )
                            )
                        }
                    }
                }
        }
    }

    private fun deleteImagesFromFirebase(images: List<String>? = null) {
        val storage = FirebaseStorage.getInstance().reference

        images?.forEach { remotePath ->
            storage.child(remotePath).delete()
        }
    }

    private fun extractImagePath(fullImageUrl : String) : String{
        val chunks = fullImageUrl.split("%2F")
        val imageName = chunks[2].split("?").first()
        return "images/${Firebase.auth.currentUser?.uid}/$imageName"
    }

}

data class UiState(
    val selectedReportId: String? = null,
    val selectedReport: Report? = null,
    val title: String = "",
    val description: String = "",
    val mood: Mood = Mood.Neutral,
    val updatedDateTime: RealmInstant? = null
)