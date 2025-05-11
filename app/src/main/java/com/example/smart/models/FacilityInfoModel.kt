package com.example.smart.models

import android.net.Uri

data class FacilityInfoModel(
    val roomNumber: String?,
    val issueName: String?,
    val issueStatus: String?,
    val issueDescription: String?,
    val issueSubmitterID: String?,
    val issueImageUri: String,
    val dateSubmitted: String
)
