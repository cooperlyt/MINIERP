(function(F,D){var C=["debug","info","warn","error"];
var E={debug:"debug",info:"info ",warn:"warn ",error:"error"};
var B={debug:1,info:2,warn:3,error:4};
var H={__import:function(M,L){if(M===document){return L
}var I=F();
for(var K=0;
K<L.length;
K++){if(M.importNode){I=I.add(M.importNode(L[K],true))
}else{var J=M.createElement("div");
J.innerHTML=L[K].outerHTML;
for(var N=J.firstChild;
N;
N=N.nextSibling){I=I.add(N)
}}}return I
},__getStyles:function(){var J=F("head");
if(J.length==0){return""
}try{var K=J.clone();
if(K.children().length==J.children().length){return K.children(":not(style):not(link[rel='stylesheet'])").remove().end().html()
}else{var I=new Array();
J.children("style, link[rel='stylesheet']").each(function(){I.push(this.outerHTML)
});
return I.join("")
}}catch(L){return""
}},__openPopup:function(){if(!this.__popupWindow||this.__popupWindow.closed){this.__popupWindow=open("","_richfaces_logWindow","height=400, width=600, resizable = yes, status=no, scrollbars = yes, statusbar=no, toolbar=no, menubar=no, location=no");
var I=this.__popupWindow.document;
I.write('<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"><html xmlns="http://www.w3.org/1999/xhtml"><head>'+this.__getStyles()+"</head><body onunload='window.close()'><div id='richfaces.log' clas='rf-log rf-log-popup'></div></body></html>");
I.close();
this.__initializeControls(I)
}else{this.__popupWindow.focus()
}},__hotkeyHandler:function(I){if(I.ctrlKey&&I.shiftKey){if((this.hotkey||"l").toLowerCase()==String.fromCharCode(I.keyCode).toLowerCase()){this.__openPopup()
}}},__getTimeAsString:function(){var I=new Date();
var J=this.__lzpad(I.getHours(),2)+":"+this.__lzpad(I.getMinutes(),2)+":"+this.__lzpad(I.getSeconds(),2)+"."+this.__lzpad(I.getMilliseconds(),3);
return J
},__lzpad:function(K,L){K=K.toString();
var I=new Array();
for(var J=0;
J<L-K.length;
J++){I.push("0")
}I.push(K);
return I.join("")
},__getMessagePrefix:function(I){return E[I]+"["+this.__getTimeAsString()+"]: "
},__setLevelFromSelect:function(I){this.setLevel(I.target.value)
},__initializeControls:function(M){var K=F("#richfaces\\.log",M);
var J=K.children("button.rf-log-element");
if(J.length==0){J=F("<button type='button' name='clear' class='rf-log-element'>Clear</button>",M).appendTo(K)
}J.click(F.proxy(this.clear,this));
var N=K.children("select.rf-log-element");
if(N.length==0){N=F("<select class='rf-log-element' name='richfaces.log' />",M).appendTo(K)
}if(N.children().length==0){for(var I=0;
I<C.length;
I++){F("<option value='"+C[I]+"'>"+C[I]+"</option>",M).appendTo(N)
}}N.val(this.getLevel());
N.change(F.proxy(this.__setLevelFromSelect,this));
var L=K.children(".rf-log-contents");
if(L.length==0){L=F("<div class='rf-log-contents'></div>",M).appendTo(K)
}this.__contentsElement=L
},__append:function(I){var K=this.__contentsElement;
if(this.mode=="popup"){var J=this.__popupWindow.document;
F(J.createElement("div")).appendTo(K).append(this.__import(J,I))
}else{F(document.createElement("div")).appendTo(K).append(I)
}},__log:function(N,K){var I=this.getLevel();
if(!B[I]){if(console.log){console.log('Warning: unknown log level "'+this.getLevel()+'" - using log level "debug"')
}I="debug"
}if(B[N]<B[I]){return 
}if(this.mode=="console"){var J="RichFaces: "+K;
if(console[N]){console[N](J)
}else{if(console.log){console.log(J)
}}return 
}if(!this.__contentsElement){return 
}var L=F();
L=L.add(F("<span class='rf-log-entry-lbl rf-log-entry-lbl-"+N+"'></span>").text(this.__getMessagePrefix(N)));
var M=F("<span class='rf-log-entry-msg rf-log-entry-msg-"+N+"'></span>");
if(typeof K!="object"||!K.appendTo){M.text(K)
}else{K.appendTo(M)
}L=L.add(M);
this.__append(L)
},init:function(I){G.constructor.call(this,"richfaces.log");
this.attachToDom();
D.setLog(this);
I=I||{};
this.level=(I.level||"info").toLowerCase();
this.hotkey=I.hotkey;
this.mode=(I.mode||"inline");
if(this.mode=="console"){}else{if(this.mode=="popup"){this.__boundHotkeyHandler=F.proxy(this.__hotkeyHandler,this);
F(document).bind("keydown",this.__boundHotkeyHandler)
}else{this.__initializeControls(document)
}}},destroy:function(){D.setLog(null);
if(this.__popupWindow){this.__popupWindow.close()
}this.__popupWindow=null;
if(this.__boundHotkeyHandler){F(document).unbind("keydown",this.__boundHotkeyHandler);
this.__boundHotkeyHandler=null
}this.__contentsElement=null;
G.destroy.call(this)
},setLevel:function(I){this.level=I;
this.clear()
},getLevel:function(){return this.level||"info"
},clear:function(){if(this.__contentsElement){this.__contentsElement.children().remove()
}}};
for(var A=0;
A<C.length;
A++){H[C[A]]=(function(){var I=C[A];
return function(J){this.__log(I,J)
}
}())
}D.HtmlLog=D.BaseComponent.extendClass(H);
var G=D.HtmlLog.$super;
F(document).ready(function(){if(typeof jsf!="undefined"){(function(N,L,I){var P=function(R){var Q="<"+R.tagName.toLowerCase();
var S=N(R);
if(S.attr("id")){Q+=(" id="+S.attr("id"))
}if(S.attr("class")){Q+=(" class="+S.attr("class"))
}Q+=" ...>";
return Q
};
var M=function(Q,S){var R=N(S);
Q.append("Element <b>"+S.nodeName+"</b>");
if(R.attr("id")){Q.append(document.createTextNode(" for id="+R.attr("id")))
}N(document.createElement("br")).appendTo(Q);
N("<span class='rf-log-entry-msg-xml'></span>").appendTo(Q).text(R.toXML());
N(document.createElement("br")).appendTo(Q)
};
var O=function(Q){var R=N(document.createElement("span"));
Q.children().each(function(){var S=N(this);
if(S.is("changes")){R.append("Listing content of response <b>changes</b> element:<br />");
S.children().each(function(){M(R,this)
})
}else{M(R,this)
}});
return R
};
var K=function(U){try{var S=L.log;
var Q=U.source;
var X=U.type;
var Z=U.responseCode;
var Y=U.responseXML;
var W=U.responseText;
if(X!="error"){S.info("Received '"+X+"' event from "+P(Q));
if(X=="beforedomupdate"){var T;
if(Y){T=N(Y).children("partial-response")
}var a=N("<span>Server returned responseText: </span><span class='rf-log-entry-msg-xml'></span>").eq(1).text(W).end();
if(T&&T.length){S.debug(a);
S.info(O(T))
}else{S.info(a)
}}}else{var R=U.status;
S.error("Received '"+X+"@"+R+"' event from "+P(Q));
var b="[status="+U.responseCode+"] ";
if(U.errorName&&U.errorMessage){b+=" "+U.errorName+": "+U.errorMessage
}else{if(U.description){b+=" "+U.description
}else{b+=" no error details"
}}S.error(b)
}}catch(V){}};
var J=L.createJSFEventsAdapter({begin:K,beforedomupdate:K,success:K,complete:K,error:K});
I.ajax.addOnEvent(J);
I.ajax.addOnError(J)
}(F,D,jsf))
}})
}(RichFaces.jQuery,RichFaces));