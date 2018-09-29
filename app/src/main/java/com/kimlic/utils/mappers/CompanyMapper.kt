package com.kimlic.utils.mappers

import com.kimlic.db.entity.Company
import com.kimlic.vendors.entity.Company_

class CompanyMapper : BaseMapper<Company_, Company> {

    // Live

    override fun transform(input: Company_): Company {
        val company = Company()
        with(company) {
            address = input.address
            details = input.details
            email = input.email
            id = input.id
            logo = input.logo
            name = input.name
            phone = input.name
            website = input.website
        }
        return company
    }
}