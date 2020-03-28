package com.notably.logic.parser.suggestion;

import com.notably.logic.commands.suggestion.SuggestionCommand;
import com.notably.logic.parser.exceptions.ParseException;

/**
 * Represents a Suggestion Command Parser that is able to parse user input into a SuggestionCommand.
 * @param <T> Generic type of SuggestionCommand.
 */
public interface SuggestionCommandParser<T extends SuggestionCommand> {
    /**
     * Parses user input into a suggestion command and returns it.
     * @param userInput The user input.
     * @return The suggestion command.
     * @throws ParseException if {@code userInput} does not conform to the expected format.
     */
    T parse(String userInput) throws ParseException;
}