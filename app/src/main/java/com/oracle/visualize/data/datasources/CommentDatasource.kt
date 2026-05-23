package com.oracle.visualize.data.datasources

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.oracle.visualize.data.datasources.dtos.CommentDTO
import com.oracle.visualize.data.datasources.dtos.ThreadDTO
import com.oracle.visualize.domain.exceptions.AppError
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Data source for visualization comments using Firestore.
 *
 * Comments are stored as a subcollection inside a visualization.
 *
 */

class CommentDatasource @Inject constructor(
    private val db: FirebaseFirestore
) {

    private fun commentsRef(visualizationId: String) =
        db.collection("visualizations")
            .document(visualizationId)
            .collection("comments")

    suspend fun createComment(
        visualizationId: String,
        commentDTO: CommentDTO
    ) {
        val docRef = commentsRef(visualizationId).document()

        val formattedComment = hashMapOf(
            "authorId" to commentDTO.authorId,
            "authorName" to commentDTO.authorName,
            "authorImageUrl" to commentDTO.authorImageUrl,
            "content" to commentDTO.content,
            "imageUrl" to commentDTO.imageUrl,
            "createdAt" to Timestamp.now()
        )
        docRef.set(formattedComment).await()
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

}
