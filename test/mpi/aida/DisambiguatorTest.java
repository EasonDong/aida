package mpi.aida;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import mpi.aida.config.AidaConfig;
import mpi.aida.config.settings.DisambiguationSettings;
import mpi.aida.config.settings.PreparationSettings;
import mpi.aida.config.settings.disambiguation.CocktailPartyDisambiguationSettings;
import mpi.aida.data.DisambiguationResults;
import mpi.aida.data.Entity;
import mpi.aida.data.PreparedInput;
import mpi.aida.data.ResultMention;
import mpi.aida.preparation.mentionrecognition.FilterMentions.FilterType;

import org.junit.Test;

/**
 * Testing against the predefined DataAccessForTesting.
 * 
 */
public class DisambiguatorTest {
  public static final double DEFAULT_ALPHA = 0.6;
  public static final double DEFAULT_COH_ROBUSTNESS = 0.9;
  public static final int DEFAULT_SIZE = 5;
  
  public DisambiguatorTest() {
    AidaConfig.set("dataAccess", "testing");
  }
  
  @Test
  public void testPageKashmir() throws Exception {
    Preparator p = new Preparator();

    String docId = "testPageKashmir1";
    String content = "When [[Page]] played Kashmir at Knebworth, his Les Paul was uniquely tuned.";
    PreparationSettings prepSettings = new PreparationSettings();
    prepSettings.setMentionsFilter(FilterType.Hybrid);

    PreparedInput preparedInput = p.prepare(docId, content, new PreparationSettings());

    DisambiguationSettings settings = new CocktailPartyDisambiguationSettings();
    settings.setAlpha(DEFAULT_ALPHA);
    settings.setCohRobustnessThreshold(DEFAULT_COH_ROBUSTNESS);
    settings.setEntitiesPerMentionConstraint(DEFAULT_SIZE);

    Disambiguator d = new Disambiguator(preparedInput, settings);

    DisambiguationResults results = d.disambiguate();

    Map<String, String> mappings = repackageMappings(results);

    String mapped = mappings.get("Page");
    assertEquals("Jimmy_Page", mapped);

    mapped = mappings.get("Kashmir");
    assertEquals("Kashmir_(song)", mapped);

    mapped = mappings.get("Knebworth");
    assertEquals("Knebworth_Festival", mapped);

    mapped = mappings.get("Les Paul");
    assertEquals(Entity.NO_MATCHING_ENTITY, mapped);
  }
  
  @Test
  public void testNoMaxEntityRank() throws Exception {
    Preparator p = new Preparator();

    String docId = "testPageKashmir2";
    String content = "When [[Page]] played Kashmir at Knebworth, his Les Paul was uniquely tuned.";
    PreparationSettings prepSettings = new PreparationSettings();
    prepSettings.setMentionsFilter(FilterType.Hybrid);

    PreparedInput preparedInput = p.prepare(docId, content, new PreparationSettings());

    DisambiguationSettings settings = new CocktailPartyDisambiguationSettings();
    settings.setAlpha(DEFAULT_ALPHA);
    settings.setCohRobustnessThreshold(DEFAULT_COH_ROBUSTNESS);
    settings.setEntitiesPerMentionConstraint(DEFAULT_SIZE);
    settings.setMaxEntityRank(-0.1);

    Disambiguator d = new Disambiguator(preparedInput, settings);

    DisambiguationResults results = d.disambiguate();

    Map<String, String> mappings = repackageMappings(results);

    String mapped = mappings.get("Page");
    assertEquals(Entity.NO_MATCHING_ENTITY, mapped);

    mapped = mappings.get("Kashmir");
    assertEquals(Entity.NO_MATCHING_ENTITY, mapped);

    mapped = mappings.get("Knebworth");
    assertEquals(Entity.NO_MATCHING_ENTITY, mapped);

    mapped = mappings.get("Les Paul");
    assertEquals(Entity.NO_MATCHING_ENTITY, mapped);
  }
  
  @Test
  public void testTopMaxEntityRank() throws Exception {
    Preparator p = new Preparator();

    String docId = "testPageKashmir3";
    String content = "When [[Page]] played Kashmir at Knebworth, his Les Paul was uniquely tuned.";
    PreparationSettings prepSettings = new PreparationSettings();
    prepSettings.setMentionsFilter(FilterType.Hybrid);

    PreparedInput preparedInput = p.prepare(docId, content, new PreparationSettings());

    DisambiguationSettings settings = new CocktailPartyDisambiguationSettings();
    settings.setAlpha(DEFAULT_ALPHA);
    settings.setCohRobustnessThreshold(DEFAULT_COH_ROBUSTNESS);
    settings.setEntitiesPerMentionConstraint(DEFAULT_SIZE);
    settings.setMaxEntityRank(0.8);

    Disambiguator d = new Disambiguator(preparedInput, settings);

    DisambiguationResults results = d.disambiguate();

    Map<String, String> mappings = repackageMappings(results);

    String mapped = mappings.get("Page");
    assertEquals("Jimmy_Page", mapped);

    mapped = mappings.get("Kashmir");
    assertEquals("Kashmir_(song)", mapped);

    mapped = mappings.get("Knebworth");
    assertEquals(Entity.NO_MATCHING_ENTITY, mapped);

    mapped = mappings.get("Les Paul");
    assertEquals(Entity.NO_MATCHING_ENTITY, mapped);
  }

  private Map<String, String> repackageMappings(DisambiguationResults results) {
    Map<String, String> repack = new HashMap<String, String>();

    for (ResultMention rm : results.getResultMentions()) {
      repack.put(rm.getMention(), results.getBestEntity(rm).getEntity());
    }

    return repack;
  }
}
