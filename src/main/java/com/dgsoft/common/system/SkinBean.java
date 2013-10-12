package com.dgsoft.common.system;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.web.RequestParameter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 5/10/13
 * Time: 8:04 AM
 */
@Name("skinBean")
@Scope(ScopeType.SESSION)
public class SkinBean {

    private String skin;

    private List<String> skins;

    public List<String> getSkins() {
        return skins;
    }

    public String getSkin() {
        if (skin == null){
            skin = "customBlueSky";
        }
        return skin;
    }

    @RequestParameter
    private String newSkin;

    @Create
    public void initialize(){
        skins = new ArrayList<String>();
        skins.add("customBlueSky");
        skins.add("customClassic");
        skins.add("customDeepMarine");
        skins.add("customEmeraldTown");
        skins.add("customJapanCherry");
        skins.add("customRuby");
        skins.add("customWine");
    }

    public void skinChange(){
        skin = newSkin;
    }

}
