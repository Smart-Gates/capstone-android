package com.capstone.ui.organization

import androidx.lifecycle.ViewModel
import com.capstone.api.response.OrganizationResponse

class OrganizationViewModel : ViewModel() {
    // TODO: Implement the ViewModel
    private var organization : OrganizationResponse? = null

    fun setorganization (initOrganizationResponse: OrganizationResponse?) {
        organization = initOrganizationResponse
    }

    fun  getorganization(): OrganizationResponse? {
        return organization
    }
}
