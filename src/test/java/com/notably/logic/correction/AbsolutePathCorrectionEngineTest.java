package com.notably.logic.correction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.notably.commons.path.AbsolutePath;
import com.notably.model.Model;
import com.notably.model.ModelManager;
import com.notably.model.block.BlockModel;
import com.notably.model.suggestion.SuggestionModel;
import com.notably.model.suggestion.SuggestionModelImpl;
import com.notably.model.viewstate.ViewStateModel;
import com.notably.model.viewstate.ViewStateModelImpl;
import com.notably.testutil.TypicalBlockModel;

public class AbsolutePathCorrectionEngineTest {
    private static Model model;

    @BeforeAll
    public static void setUp() {
        BlockModel blockModel = TypicalBlockModel.getTypicalBlockModel();
        SuggestionModel suggestionModel = new SuggestionModelImpl();
        ViewStateModel viewStateModel = new ViewStateModelImpl();
        model = new ModelManager(blockModel, suggestionModel, viewStateModel);
    }

    @Test
    public void constructor_nullModel_exceptionThrown() {
        assertThrows(NullPointerException.class, () ->
                new AbsolutePathCorrectionEngine(null, new CorrectionEngineParameters()));
    }

    @Test
    public void constructor_negativeDistanceThreshold_exceptionThrown() {
        final int negativeDistanceThreshold = -1;

        assertThrows(IllegalArgumentException.class, () -> new AbsolutePathCorrectionEngine(model,
                new CorrectionEngineParameters().setDistanceThreshold(negativeDistanceThreshold)));
    }

    @Test
    public void constructor_negativeForwardMatchingThreshold_exceptionThrown() {
        final int negativeForwardMatchingThreshold = -1;

        assertThrows(IllegalArgumentException.class, () -> new AbsolutePathCorrectionEngine(model,
                new CorrectionEngineParameters().setForwardMatchingThreshold(negativeForwardMatchingThreshold)));
    }

    @Test
    public void correct_noForwardMatchingAndWithinDistanceThreshold_correctionDone() {
        final int distanceThreshold = 2;
        final AbsolutePathCorrectionEngine correctionEngine = new AbsolutePathCorrectionEngine(model,
                new CorrectionEngineParameters().setForwardMatching(false).setDistanceThreshold(distanceThreshold));
        final AbsolutePath uncorrectedInput = AbsolutePath.fromString("/y2s2/Cs2103t/Tutorials/tutorial");

        final List<AbsolutePath> expectedCorrectedItems = List.of(TypicalBlockModel.PATH_TO_CS2103T_TUTORIAL_1,
                TypicalBlockModel.PATH_TO_CS2103T_TUTORIAL_2);
        final CorrectionStatus expectedCorrectedStatus = CorrectionStatus.CORRECTED;
        final CorrectionResult<AbsolutePath> expectedCorrectionResult = new CorrectionResult<>(
                expectedCorrectedStatus, expectedCorrectedItems);

        CorrectionResult<AbsolutePath> correctionResult = correctionEngine.correct(uncorrectedInput);
        assertEquals(expectedCorrectionResult, correctionResult);
    }

    @Test
    public void correct_noForwardMatchingAndExceedDistanceThreshold_correctionFailed() {
        final int distanceThreshold = 1;
        final AbsolutePathCorrectionEngine correctionEngine = new AbsolutePathCorrectionEngine(model,
                new CorrectionEngineParameters().setForwardMatching(false).setDistanceThreshold(distanceThreshold));
        final AbsolutePath uncorrectedInput = AbsolutePath.fromString("/Y2s2/Cs");

        final CorrectionStatus expectedCorrectedStatus = CorrectionStatus.FAILED;
        final CorrectionResult<AbsolutePath> expectedCorrectionResult = new CorrectionResult<>(expectedCorrectedStatus);

        CorrectionResult<AbsolutePath> correctionResult = correctionEngine.correct(uncorrectedInput);
        assertEquals(expectedCorrectionResult, correctionResult);
    }

    @Test
    public void correct_noForwardMatchingAndExactMatch_noCorrection() {
        final int distanceThreshold = 1;
        final AbsolutePathCorrectionEngine correctionEngine = new AbsolutePathCorrectionEngine(model,
                new CorrectionEngineParameters().setForwardMatching(false).setDistanceThreshold(distanceThreshold));
        final AbsolutePath uncorrectedInput = AbsolutePath.fromString("/Y2s2/cs2106");

        final List<AbsolutePath> expectedCorrectedItems = List.of(TypicalBlockModel.PATH_TO_CS2106);
        final CorrectionStatus expectedCorrectionStatus = CorrectionStatus.UNCHANGED;
        final CorrectionResult<AbsolutePath> expectedCorrectionResult = new CorrectionResult<>(
                expectedCorrectionStatus, expectedCorrectedItems);

        CorrectionResult<AbsolutePath> correctionResult = correctionEngine.correct(uncorrectedInput);
        assertEquals(expectedCorrectionResult, correctionResult);
    }

    @Test
    public void correct_withForwardMatchingAndValidPrefixAndBelowForwardMatchingThreshold_forwardMatchingDone() {
        final int distanceThreshold = 2;
        final int forwardMatchingThreshold = 2;
        final AbsolutePathCorrectionEngine correctionEngine = new AbsolutePathCorrectionEngine(model,
                new CorrectionEngineParameters()
                    .setForwardMatching(true)
                    .setDistanceThreshold(distanceThreshold)
                    .setForwardMatchingThreshold(forwardMatchingThreshold));
        final AbsolutePath uncorrectedInput = AbsolutePath.fromString("/Y");

        final List<AbsolutePath> expectedCorrectedItems = List.of(TypicalBlockModel.PATH_TO_Y2S2);
        final CorrectionStatus expectedCorrectionStatus = CorrectionStatus.CORRECTED;
        final CorrectionResult<AbsolutePath> expectedCorrectionResult = new CorrectionResult<>(
                expectedCorrectionStatus, expectedCorrectedItems);

        CorrectionResult<AbsolutePath> correctionResult = correctionEngine.correct(uncorrectedInput);
        assertEquals(expectedCorrectionResult, correctionResult);
    }

    @Test
    public void correct_withForwardMatchingAndInvalidPrefixAndBelowForwardMatchingThreshold_noForwardMatching() {
        final int distanceThreshold = 2;
        final int forwardMatchingThreshold = 2;
        final AbsolutePathCorrectionEngine correctionEngine = new AbsolutePathCorrectionEngine(model,
                new CorrectionEngineParameters()
                    .setForwardMatching(true)
                    .setDistanceThreshold(distanceThreshold)
                    .setForwardMatchingThreshold(forwardMatchingThreshold));
        final AbsolutePath uncorrectedInput = AbsolutePath.fromString("/Z");

        final List<AbsolutePath> expectedCorrectedItems = List.of(TypicalBlockModel.PATH_TO_ROOT);
        final CorrectionStatus expectedCorrectionStatus = CorrectionStatus.CORRECTED;
        final CorrectionResult<AbsolutePath> expectedCorrectionResult = new CorrectionResult<>(
                expectedCorrectionStatus, expectedCorrectedItems);

        CorrectionResult<AbsolutePath> correctionResult = correctionEngine.correct(uncorrectedInput);
        assertEquals(expectedCorrectionResult, correctionResult);
    }

    @Test
    public void correct_withForwardMatchingAndWithinThresholds_forwardMatchingAndCorrectionDone() {
        final int distanceThreshold = 2;
        final int forwardMatchingThreshold = 2;
        final AbsolutePathCorrectionEngine correctionEngine = new AbsolutePathCorrectionEngine(model,
                new CorrectionEngineParameters()
                    .setForwardMatching(true)
                    .setDistanceThreshold(distanceThreshold)
                    .setForwardMatchingThreshold(forwardMatchingThreshold));
        final AbsolutePath uncorrectedInput = AbsolutePath.fromString("/Y2S2/cs2105");

        final List<AbsolutePath> expectedCorrectedItems = List.of(TypicalBlockModel.PATH_TO_CS2103T,
                TypicalBlockModel.PATH_TO_CS2106);
        final CorrectionStatus expectedCorrectionStatus = CorrectionStatus.CORRECTED;
        final CorrectionResult<AbsolutePath> expectedCorrectionResult = new CorrectionResult<>(
                expectedCorrectionStatus, expectedCorrectedItems);

        CorrectionResult<AbsolutePath> correctionResult = correctionEngine.correct(uncorrectedInput);
        assertEquals(expectedCorrectionResult, correctionResult);
    }
}

