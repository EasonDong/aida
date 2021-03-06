package mpi.aida.config.settings.preparation;

import mpi.aida.config.settings.PreparationSettings;
import mpi.tokenizer.data.Tokenizer;

/**
 * Uses the caseless models of Stanford NLP tools (used for e.g. tweets)
 *
 */
public class EnglishCaselessStanfordPreparationSettings extends PreparationSettings {
  private static final long serialVersionUID = -6235813561267960131L;
  
  public EnglishCaselessStanfordPreparationSettings() {
    this.setTokenizerType(Tokenizer.type.ENGLISH_CASELESS_TOKENS);
  }
  
  
}
