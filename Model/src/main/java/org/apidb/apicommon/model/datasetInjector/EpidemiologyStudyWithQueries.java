package org.apidb.apicommon.model.datasetInjector;

import org.apidb.apicommon.model.datasetInjector.EpidemiologyStudy;
import java.util.Map;
import java.util.HashMap;

public class EpidemiologyStudyWithQueries extends EpidemiologyStudy {


    @Override
    protected Map<String,String[]> householdQuestionTemplateNamesToScopes() {
    Map<String,String[]> map = new HashMap<String,String[]>();
    boolean hasHouseholdQuestion = getPropValueAsBoolean("hasHouseholdQuestion");
    boolean hasHouseholds = getPropValueAsBoolean("hasHouseholdRecord");
    if(hasHouseholdQuestion && hasHouseholds){
        map.put("HouseholdsByMetadata", new String[] {"menu", "webservice"});
    }
    return(map);
    }

    @Override
    protected Map<String,String[]> participantQuestionTemplateNamesToScopes() {
      Map<String,String[]> map = new HashMap<String,String[]>();
      map.put("ParticipantsByMetadata", new String[] {"menu", "webservice"});
      return(map);
    }

    @Override
    protected Map<String,String[]> observationQuestionTemplateNamesToScopes() {
      Map<String,String[]> map = new HashMap<String,String[]>();
      boolean hasObservationQuestion = getPropValueAsBoolean("hasObservationQuestion");
      boolean hasObservations = getPropValueAsBoolean("hasObservationRecord");
      if(hasObservationQuestion && hasObservations){
          map.put("ObservationsByMetadata", new String[] {"menu", "webservice"});
      }
      return(map);
    }

    @Override
    protected String participantGraphAttributesTemplateName() {
        return(getPropValue("participantGraphAttributesTemplateName"));
    }


    @Override
    protected Map<String,String[]> participantGraphAttributesToScopes() {
      String presenterId = getPropValue("presenterId");
      Map<String,String[]> scopeMap = new HashMap<String,String[]>();
      //Add statement here for each study with graphs
      if(presenterId.equals("DS_05ea525fd3")){
          scopeMap.put("malaria_cat_compact", new String[] {"results"});
          scopeMap.put("malaria_cat_svg", new String[] {"record"});
      }
      return(scopeMap);
    }

  @Override
  public void injectTemplates() {
      String tblPrefix = "D" + getPropValue("datasetDigest");
      String presenterId = getPropValue("presenterId");
      String studyType = getPropValue("studyType");
      String firstWizardStep = getPropValue("firstWizardStep");
      Boolean keepRegionInHouseholdFilter = getPropValueAsBoolean("keepRegionInHouseholdFilter");
      if(firstWizardStep.equals("Household") || keepRegionInHouseholdFilter){
          setPropValue("rmRegionSqlCommentStart","/*");
          setPropValue("rmRegionSqlCommentEnd","*/");
      }else{
          setPropValue("rmRegionSqlCommentStart","");
          setPropValue("rmRegionSqlCommentEnd","");
      }
      String cardQuestions = "UNION select '" + presenterId + "' as dataset_presenter_id, 'cardQuestions' as property, '{ ";
      
      super.injectTemplates();  

      boolean hasHouseholdQuestion = getPropValueAsBoolean("hasHouseholdQuestion");
      boolean hasParticipantQuestion = getPropValueAsBoolean("hasParticipantQuestion");
      boolean hasObservationQuestion = getPropValueAsBoolean("hasObservationQuestion");
      boolean hasHouseholds = getPropValueAsBoolean("hasHouseholdRecord");
      boolean hasParticipants = getPropValueAsBoolean("hasParticipantRecord");
      boolean hasObservations = getPropValueAsBoolean("hasObservationRecord");
      String householdRecordClass = makeRecordClassName(HOUSEHOLD_RECORD_CLASS_PREFIX);
      String participantRecordClass = makeRecordClassName(PARTICIPANT_RECORD_CLASS_PREFIX);
      String observationRecordClass = makeRecordClassName(OBSERVATION_RECORD_CLASS_PREFIX);

      //Inject the filter param queries for participants if any questions generated
      if(hasParticipantQuestion || hasHouseholdQuestion || hasObservationQuestion){
          String householdMultiFilterIdsQuoted = addQuotes(getPropValue("householdMultiFilterIds"));
          String householdFilterExcludedIdsQuoted = addQuotes(getPropValue("householdFilterExcludedIds"));
          String participantMultiFilterIdsQuoted = addQuotes(getPropValue("participantMultiFilterIds"));
          String participantFilterExcludedIdsQuoted = addQuotes(getPropValue("participantFilterExcludedIds"));
          String observationMultiFilterIdsQuoted = addQuotes(getPropValue("observationMultiFilterIds"));
          String observationFilterExcludedIdsQuoted = addQuotes(getPropValue("observationFilterExcludedIds"));
          
          if(householdMultiFilterIdsQuoted == null || householdMultiFilterIdsQuoted.equals("''")) {
              householdMultiFilterIdsQuoted = "'NA'";
          }
          setPropValue("householdMultiFilterIdsQuoted", householdMultiFilterIdsQuoted);
          if(householdFilterExcludedIdsQuoted == null || householdFilterExcludedIdsQuoted.equals("''")) {
              householdFilterExcludedIdsQuoted  = "'NA'";
          }
          setPropValue("householdFilterExcludedIdsQuoted", householdFilterExcludedIdsQuoted);
          if(participantMultiFilterIdsQuoted == null || participantMultiFilterIdsQuoted.equals("''")) {
              participantMultiFilterIdsQuoted  = "'NA'";
          }
          setPropValue("participantMultiFilterIdsQuoted", participantMultiFilterIdsQuoted);
          if(participantFilterExcludedIdsQuoted == null || participantFilterExcludedIdsQuoted.equals("''")) {
              participantFilterExcludedIdsQuoted  = "'NA'";
          }
          setPropValue("participantFilterExcludedIdsQuoted", participantFilterExcludedIdsQuoted);
          if(observationMultiFilterIdsQuoted == null || observationMultiFilterIdsQuoted.equals("''")) {
              observationMultiFilterIdsQuoted  = "'NA'";
          }
          setPropValue("observationMultiFilterIdsQuoted", observationMultiFilterIdsQuoted);
          if(observationFilterExcludedIdsQuoted == null || observationFilterExcludedIdsQuoted.equals("''")) {
              observationFilterExcludedIdsQuoted  = "'NA'";
          }
          setPropValue("observationFilterExcludedIdsQuoted", observationFilterExcludedIdsQuoted);
          
          injectTemplate("participantFilterParamQueries" + firstWizardStep);
      }


      if(hasParticipantQuestion && hasParticipants){
          injectTemplate("participantFilterParams" + firstWizardStep);
          //Inject the particiant metadata query
          injectTemplate("participant" + studyType + "Query" + firstWizardStep);
          
          cardQuestions = cardQuestions + " \"participants\": \"ParticipantQuestions." + presenterId + "ParticipantsByMetadata\"";
      }

      //now to do observations
      if(hasObservationQuestion && hasObservations){
          //Inject the metadata query
          injectTemplate("observation" + studyType + "Query" + firstWizardStep);
          
          //Inject the filter params .... note these use the ontology queries from particiants filters
          injectTemplate("observationFilterParams" + firstWizardStep);
          injectTemplate("observationFilterParamQueries" + firstWizardStep);

          cardQuestions = cardQuestions + ", \"observations\": \"ClinicalVisitQuestions." + presenterId + "ObservationsByMetadata\"";
      }

      //and households
      if(hasHouseholdQuestion && hasHouseholds){
          //Inject the metadata query
          injectTemplate("householdMetadataQuery" + firstWizardStep);
          
          //Inject the filter params .... note these use the ontology queries from particiants filters
          injectTemplate("householdFilterParams" + firstWizardStep);
          injectTemplate("householdFilterParamQueries" + firstWizardStep);

          cardQuestions = cardQuestions + ", \"households\": \"HouseholdQuestions." + presenterId + "HouseholdsByMetadata\"";
      }
      //lastly inject the cardQuestions
      cardQuestions = cardQuestions + " }' as value from dual";
      System.err.println("cardQuestionsSql=" + cardQuestions);
      setPropValue("cardQuestionsSql",cardQuestions);
      injectTemplate("injectDatasetQuestions");
  }

  @Override
  public String[][] getPropertiesDeclaration() {

      String[][] superDeclaration = super.getPropertiesDeclaration();

      String [][] declaration = {{"householdMultiFilterIds", ""},
                                 {"householdFilterExcludedIds", ""},
                                 {"participantMultiFilterIds", ""},
                                 {"participantFilterExcludedIds", ""},
                                 {"observationMultiFilterIds", ""},
                                 {"observationFilterExcludedIds", ""},
                                 {"studyAbbreviation", ""},
                                 {"studyType", ""},
                                 {"hasParticipantQuestion", ""},
                                 {"hasHouseholdQuestion", ""},
                                 {"hasObservationQuestion", ""},
                                 {"participantGraphAttributesTemplateName", ""},
                                 {"firstWizardStep", ""},
                                 {"keepRegionInHouseholdFilter", ""},
                                 {"timeSourceId", ""}
      };

      return combinePropertiesDeclarations(superDeclaration, declaration);
  }

}