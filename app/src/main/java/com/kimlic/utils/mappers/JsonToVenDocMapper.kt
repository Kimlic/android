package com.kimlic.utils.mappers

import com.kimlic.db.entity.VendorDocument
import com.kimlic.vendors.Document_

class JsonToVenDocMapper : BaseMapper<Document_, VendorDocument> {
    override fun transform(input: Document_): VendorDocument {
        return VendorDocument(contexts = input.contexts.orEmpty(), countries = input.countries.orEmpty(), description = input.description.orEmpty(), type = input.type.orEmpty())
    }
}