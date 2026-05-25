package com.oracle.visualize.data.datasources

import android.net.Uri
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.oracle.visualize.data.datasources.dtos.CommentDTO
import com.oracle.visualize.data.datasources.dtos.ThreadDTO
import com.oracle.visualize.domain.exceptions.AppError
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

/**
 * Data source for visualization comments using Firestore.
 *
 * Comments are stored as a subcollection inside a visualization.
 *
 */

class CommentDatasource @Inject constructor(
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage
) {

    private fun commentsRef(visualizationId: String) =
        db.collection("visualizations")
            .document(visualizationId)
            .collection("comments")

    suspend fun createComment(
        visualizationId: String,
        commentDTO: CommentDTO
    ) {
        val formattedComment = hashMapOf(
            "authorID" to commentDTO.authorID,
            "content" to commentDTO.content,
            "createdAt" to Timestamp.now(),
            "imageURL" to commentDTO.imageURL
        )
        commentsRef(visualizationId)
            .document()
            .set(formattedComment)
            .await()
    }

    suspend fun getComments(visualizationId: String): List<CommentDTO> {
        val snapshot = commentsRef(visualizationId)
            .orderBy("createdAt")
            .get()
            .await()

        return snapshot.documents.map { doc ->
            doc.toObject(CommentDTO::class.java)
                ?.copy(id = doc.id)
                ?: throw AppError.ParsingError("Failed to parse comments")
        }
    }

    private fun threadsRef(
        visualizationId: String,
        commentId: String
    ) =
        commentsRef(visualizationId)
            .document(commentId)
            .collection("threads")

    suspend fun getThreads(
        visualizationId: String,
        commentId: String
    ): List<ThreadDTO> {
        val snapshot = threadsRef(
            visualizationId = visualizationId,
            commentId = commentId
        )
            .orderBy("createdAt")
            .get()
            .await()

        return snapshot.documents.map { doc ->
            doc.toObject(ThreadDTO::class.java)
                ?.copy(id = doc.id)
                ?: throw AppError.ParsingError("Failed to parse threads")
        }
    }

    suspend fun uploadSnip(userID: String, uri: Uri): String {
        val storageRef = storage.reference.child("snips/$userID/${UUID.randomUUID()}")
        storageRef.putFile(uri).await()
        return storageRef.downloadUrl.await().toString()
    }

}
