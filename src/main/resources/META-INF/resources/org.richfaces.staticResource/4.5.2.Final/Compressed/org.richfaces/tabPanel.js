(function(B,A){A.ui=A.ui||{};
var C={exec:function(E,D){if(D.switchMode=="server"){return this.execServer(E,D)
}else{if(D.switchMode=="ajax"){return this.execAjax(E,D)
}else{if(D.switchMode=="client"){return this.execClient(E,D)
}else{A.log.error("SwitchItems.exec : unknown switchMode ("+D.switchMode+")")
}}}},execServer:function(F,D){if(F){var E=F.__leave();
if(!E){return false
}}this.__setActiveItem(D);
var G={};
G[D.getTogglePanel().id]=D.name;
G[D.id]=D.id;
B.extend(G,D.getTogglePanel().options.ajax||{});
A.submitForm(this.__getParentForm(D),G);
return false
},execAjax:function(F,D){var E=B.extend({},D.getTogglePanel().options.ajax,{});
this.__setActiveItem(D);
A.ajax(D.id,null,E);
if(F){this.__setActiveItem(F)
}return false
},execClient:function(F,D){if(F){var E=F.__leave();
if(!E){return false
}}this.__setActiveItem(D);
D.__enter();
D.getTogglePanel().__fireItemChange(F,D);
return true
},__getParentForm:function(D){return B(A.getDomElement(D.id)).parents("form:first")
},__setActiveItem:function(D){A.getDomElement(D.togglePanelId+"-value").value=D.getName();
D.getTogglePanel().activeItem=D.getName()
}};
A.ui.TabPanel=A.ui.TogglePanel.extendClass({name:"TabPanel",init:function(F,E){A.ui.TogglePanel.call(this,F,E);
this.items=[];
this.isKeepHeight=E.isKeepHeight||false;
this.element=document.getElementById(F);
var D=B(this.element);
D.on("click",".rf-tab-hdr-act",B.proxy(this.__clickListener,this));
D.on("click",".rf-tab-hdr-inact",B.proxy(this.__clickListener,this))
},__clickListener:function(D){var F=B(D.target);
if(!F.hasClass("rf-tab-hdr")){F=F.parents(".rf-tab-hdr").first()
}var E=F.data("tabname");
this.switchToItem(E)
},__itemsSwitcher:function(){return C
}})
})(RichFaces.jQuery,RichFaces);