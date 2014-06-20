package mpi.aida.util.htmloutput;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


/**
 * Constructs HTML version of disambiguated results from JSON String.
 * Supports HTML construction from DisambiguationResults object as well.
 */
public class HtmlGenerator {

  private String inputFile;
  private JSONObject jsonContent;
  
  /**
   * Initializes Converter object with input details
   * 
   * @param content The original string submitted for disambiguation.
   * @param jsonRepr JSON representation of the disambiguated results.
   * @param inputFile The name of the input file submitted.
   * @param input Constructed PreparedInput object.
   */
  public HtmlGenerator(String jsonRepr, String inputFile) throws Exception {
    this.inputFile = inputFile;
    JSONParser jParser = new JSONParser();
    this.jsonContent = (JSONObject) jParser.parse(jsonRepr);
  }
  
  /**
   * This method generates HTML from given JSON representation of disambiguated result
   * 
   * @param jsonRepr A JSON string of disambiguated result.
   * @return HTML body as a string
   * @throws Exception
   */
  public String constructFromJson(String jsonRepr) throws Exception {
    //GenerateWebHtml gen = new GenerateWebHtml();
    //String html = gen.processJSON(content, input, jsonRepr, false);
    StringBuilder sb = constructHTML(null);
    sb.append("</body></html>");
    return sb.toString().replaceAll("\n", "<br />");
  }

  /**
   * This method generates Header Tag with given content and level.
   * @param content The string to be placed between heading tag
   * @param level The level of heading (1 to 6)
   * @return HTML String
   */
  public String generateHeading(String content, int level){
      String tagVal = "h"+level;
      String res = getTag(tagVal, false);
      res = res.concat(content);
      res = res.concat(getTag(tagVal, true));
      return res;
  }

  /**
   * Generates HTML snippet of annotated text with all hyperlinks.
   * @return HTML snippet for annotated string.
   */
  public String getAnnotatedText(){
    Map<String, String> url2rep = new HashMap<String, String>();
    JSONObject entities = (JSONObject) jsonContent.get("entityMetadata");
    for (Object o : entities.keySet()) {
      String id = (String) o;
      url2rep.put(
          (String) ((JSONObject) entities.get(id)).get("url"),
          (String) ((JSONObject) entities.get(id)).get("readableRepr"));
    }
    String annotatedText = (String)jsonContent.get("annotatedText");    
    Pattern pattern = Pattern.compile("\\[\\[([^|]+)\\|([^]]+)\\]\\]");
    Matcher matcher = pattern.matcher(annotatedText);
    StringBuffer sb =  new StringBuffer();
    int current = 0;
    while (matcher.find()){
      sb.append(annotatedText.substring(current, matcher.start()));
      String url = matcher.group(1);
      String mention = matcher.group(2);
      String representation = url2rep.get(url);
      sb.append("<span class='eq'>" + mention + "</span>");
      sb.append(" <small>[<a href=" + url + ">" + representation + "</a>]</small>");
      current = matcher.end();
    }
    sb.append(annotatedText.substring(current, annotatedText.length()));
    return sb.toString();
  }

  /**
   * Generates HTML list items based on the mention-entity result from json.
   * @return HTML snippet
   */
  @SuppressWarnings("rawtypes")
  public String generateMEHtml(){
    JSONArray mentions = (JSONArray)jsonContent.get("mentions");
    Iterator itMention = mentions.iterator();
    StringBuilder htmlList = new StringBuilder();
    //htmlList.append(generateHeading("Mappings", 2));
    htmlList.append(getTag("ul", false));
    while(itMention.hasNext()){
      htmlList.append(getTag("li", false));
      JSONObject tmpMention = (JSONObject)itMention.next();
      htmlList.append("["+(String)jsonContent.get("docID")+"] ");
      htmlList.append((String)tmpMention.get("name")+" ( ");
      htmlList.append((Long)tmpMention.get("length")+"/");
      htmlList.append((Long)tmpMention.get("offset")+") -> [");
      JSONArray entities = (JSONArray)tmpMention.get("allEntities");
      for (int i = 0; i < entities.size(); ++i) {
        JSONObject entity = (JSONObject) entities.get(i);
        htmlList.append((String)entity.get("kbIdentifier"));
        htmlList.append("("+(String)entity.get("disambiguationScore")+")");
        if(i < entities.size() - 1) {
          htmlList.append(", ");
        }
      }
      htmlList.append("]");
      htmlList.append(getTag("li",true));
    }
    htmlList.append(getTag("ul", true));
    return htmlList.toString();
  }

  private String getTag(String tagValue, boolean endTag){
    StringBuilder sb = new StringBuilder();
    sb.append("<");
    if(endTag)
      sb.append("/");
    sb.append(tagValue);
    sb.append(">");
    return sb.toString();
  }

  private StringBuilder constructHTML(String html) {
    StringBuilder sb = new StringBuilder();
    sb.append("<html><head><title>").append(inputFile).append("</title>");
    sb.append("<meta http-equiv='content-type'");
    sb.append("CONTENT='text/html; charset=utf-8' />");
    sb.append("<style type='text/css'>");
    sb.append(".eq { background-color:#87CEEB } ");
    sb.append("</style>").append("<body>");
    sb.append(generateHeading(inputFile, 1));
    sb.append("\n");
    sb.append(generateHeading("AnotatedText", 2));
    sb.append(getAnnotatedText());
    sb.append("\n");
    if(html!=null)
      sb.append(html);
    else
    {
      sb.append("<h2>All Mappings</h2>");
      sb.append(generateMEHtml());
    }
    return sb;
  }
}