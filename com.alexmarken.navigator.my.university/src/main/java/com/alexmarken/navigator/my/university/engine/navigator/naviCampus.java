package com.alexmarken.navigator.my.university.engine.navigator;

import com.alexmarken.navigator.my.university.util.Parser.URLData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Alex Marken on 06.11.2015.
 */

public class naviCampus {
    private static String LOG_TAG = naviCampus.class.getName();

    public ArrayList<naviCorp> corplist = new ArrayList<naviCorp>();
    public ArrayList<naviMap> maps = new ArrayList<naviMap>();

    public naviCampus() {
        parseCorpuslist();
    }

    public ArrayList<naviAuditor> getAuditors() {
        ArrayList<naviAuditor> result = new ArrayList<naviAuditor>();

        for(int i = 0; i < corplist.size(); i++)
            for(int j = 0; j < corplist.get(i).getAuditors().size(); j++)
                if (corplist.get(i).getAuditors().get(j).getAuditor() != null)
                    result.add(corplist.get(i).getAuditors().get(j));

        return result;
    }

    public naviCorp getCorpus(int num) {
        naviCorp result = null;

        for(int i = 0; (i < getCount()) && (result == null); i++)
            if (corplist.get(i).getId() == num)
                result = corplist.get(i);

        return result;
    }

    public naviMap getMap(int corpus, int stage) {
        naviMap find = null;

        for(int i = 0; (i < maps.size()) && (find == null); i++) {
            if ((corpus == maps.get(i).corpus) && (stage == maps.get(i).stage))
                find = maps.get(i);
        }

        return find;
    }

    public int getCount() {
        return corplist.size();
    }

    private void parseCorpuslist() {
        URLData data = new URLData("http://83.220.169.40:1342/MyUniversity/api/v1.0/getUniversityInfo");
        data.addParam("universityAcronym", "OMGUPS");
        data.taskEvents = new URLData.TaskEvents() {

            @Override
            public void onTaskStart() {

            }

            @Override
            public void onTaskFinish(JSONObject obj) {
                try {
                    JSONArray jsonCorplist = obj.getJSONArray("corplist");

                    for(int i = 0; i < jsonCorplist.length(); i++) {
                        JSONObject jsonCorpus = jsonCorplist.getJSONObject(i);

                        naviCorp corp = new naviCorp(jsonCorpus.getInt("id"), jsonCorpus.getString("CorpusName"), jsonCorpus.getInt("StageCount"),
                                jsonCorpus.getInt("isGround"), jsonCorpus.getInt("X"), jsonCorpus.getInt("Y"));


                        corplist.add(corp);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }
}
