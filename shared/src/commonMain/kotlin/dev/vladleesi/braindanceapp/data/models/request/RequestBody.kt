package dev.vladleesi.braindanceapp.data.models.request

data class RequestBody(
    val body: String,
) {
    class Builder(
        builder: Builder.() -> Unit,
    ) {
        var fields: List<String> = emptyList()
        var where: List<String> = emptyList()
        var sort: String = ""
        var limit: Int = LIMIT_SIZE_DEFAULT

        init {
            builder.invoke(this)
        }

        fun build(): RequestBody {
            val bodyStringBuilder =
                StringBuilder().apply {
                    if (fields.isNotEmpty()) {
                        append("fields ${fields.joinToString(separator = ",")};")
                    }
                    if (where.isNotEmpty()) {
                        append("where ${where.joinToString(separator = " ")};")
                    }
                    if (sort.isNotEmpty()) {
                        append("sort $sort;")
                    }
                    if (limit > 0) {
                        append("limit $limit;")
                    }
                }
            return RequestBody(bodyStringBuilder.toString().trimIndent())
        }

        private companion object {
            private const val LIMIT_SIZE_DEFAULT = 15
        }
    }

    enum class Where(
        val operator: String,
    ) {
        AND("&"),
        OR("|"),
    }

    enum class Sort(
        val order: String,
    ) {
        ASC("asc"),
        DESC("desc"),
    }
}
