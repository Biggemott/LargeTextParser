package com.biggemot.largetextparser.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import timber.log.Timber
import java.util.regex.Pattern
import javax.inject.Inject

class ParserInteractorImpl @Inject constructor(
    private val repo: ParserRepo
) : ParserInteractor {

    companion object {
        private val ESCAPE_REGEX_CHARS = Pattern.compile("[{}()\\[\\].+^$\\\\|]")
    }

    override fun parseFile(url: String, pattern: String): Flow<ParseResult> {
        repo.clearLog()
        val regex = prepareRegex(pattern)
        return repo.getFileStream(url).filter { parseResult ->
            if (parseResult is ParseResult.Data) {
                regex.matches(parseResult.value).also { matches->
                    if (matches) {
                        Timber.d("matches: %s", parseResult.value)
                        repo.appendLogLine(parseResult.value)
                    }
                }
            } else {
                Timber.d(parseResult.toString())
                true
            }
        }
    }

    override fun copyToClipboard(strings: List<String>) {
        repo.copyToClipboard(strings)
    }

    private fun prepareRegex(input: String): Regex {
        // escaping all special chars except '*' and '?'
        val prepared = ESCAPE_REGEX_CHARS.matcher(input).replaceAll("\\\\$0")
            .replace("*", ".*") // any char any number of times
            .replace("?", ".") // any char one time
        return Regex("^$prepared$") // '^' and '$' used to match the whole line
    }
}