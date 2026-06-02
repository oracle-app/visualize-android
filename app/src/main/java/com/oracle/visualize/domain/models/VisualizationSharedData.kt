package com.oracle.visualize.domain.models

/**
 * Lightweight domain model representing only the sharing metadata of a visualization.
 * Used by [ShareWithTeammatesViewModel] to pre-populate the share screen
 * with the existing list of recipients without loading the full visualization.
 *
 * @property sharedWithUsers List of user IDs that currently have access.
 * @property sharedWithTeams List of team IDs that currently have access.
 */
data class VisualizationSharedData(
    val sharedWithUsers: List<String>,
    val sharedWithTeams: List<String>
)
