package org.apidb.apicommon.model.datasetInjector.custom.Gates;

import org.apidb.apicommon.model.datasetInjector.EpidemiologyStudy;
import java.util.Map;
import java.util.HashMap;

public class GEMS1aCaseControl extends EpidemiologyStudy {

    @Override
    protected void setStudySpecificProperties(){
        setPropValue("injectParams","false");
    }  

    @Override
    protected Map<String,String[]> householdQuestionTemplateNamesToScopes() {
      return(new HashMap<String,String[]>());
    }
    @Override
    protected Map<String,String[]> participantQuestionTemplateNamesToScopes() {
      Map<String,String[]> map = new HashMap<String,String[]>();
      map.put("ParticipantsByCaseControlVisits_gems1a", new String[] {"menu", "webservice"});
      //      map.put("ParticipantsByCaseControlVisitsShort_gems", new String[] {"menu", "webservice"});

      return(map);
    }

    @Override
    protected Map<String,String[]> observationQuestionTemplateNamesToScopes() {
      return(new HashMap<String,String[]>());
    }

        
    @Override
    protected String participantGraphAttributesTemplateName() {
      return("");
    }


    @Override
    protected Map<String,String[]> participantGraphAttributesToScopes() {
      return(new HashMap<String,String[]>());
    }


}
