

window.RichFaces=window.RichFaces||{};
RichFaces.jQuery=RichFaces.jQuery||window.jQuery;
(function($,rf){rf.RICH_CONTAINER="rf";
rf.EDITABLE_INPUT_SELECTOR=":not(:submit):not(:button):not(:image):input:visible:enabled";
rf.KEYS={BACKSPACE:8,TAB:9,RETURN:13,ESC:27,PAGEUP:33,PAGEDOWN:34,END:35,HOME:36,LEFT:37,UP:38,RIGHT:39,DOWN:40,DEL:46};
if(window.jsf){var jsfAjaxRequest=jsf.ajax.request;
var jsfAjaxResponse=jsf.ajax.response
}rf.getDomElement=function(source){var type=typeof source;
var element;
if(source==null){element=null
}else{if(type=="string"){element=document.getElementById(source)
}else{if(type=="object"){if(source.nodeType){element=source
}else{if(source instanceof $){element=source.get(0)
}}}}}return element
};
rf.component=function(source){var element=rf.getDomElement(source);
if(element){return $(element).data("rf.widget")||(element[rf.RICH_CONTAINER]||{})["component"]
}};
rf.$=function(source){rf.log.warn("The function `RichFaces.$` has been deprecated and renamed to `RichFaces.component`.  Please adjust your usage accordingly.");
return rf.component.call(this,source)
};
$.extend($.expr[":"],{editable:function(element){return $(element).is(rf.EDITABLE_INPUT_SELECTOR)
}});
rf.$$=function(componentName,element){while(element.parentNode){var e=element[rf.RICH_CONTAINER];
if(e&&e.component&&e.component.name==componentName){return e.component
}else{element=element.parentNode
}}};
rf.findNonVisualComponents=function(source){var element=rf.getDomElement(source);
if(element){return(element[rf.RICH_CONTAINER]||{})["attachedComponents"]
}};
rf.invokeMethod=function(source,method){var c=rf.component(source);
var f;
if(c&&typeof (f=c[method])=="function"){return f.apply(c,Array.prototype.slice.call(arguments,2))
}};
rf.cleanComponent=function(source){var component=rf.component(source);
if(component&&!$(source).data("rf.bridge")){component.destroy();
component.detach(source)
}var attachedComponents=rf.findNonVisualComponents(source);
if(attachedComponents){for(var i in attachedComponents){if(attachedComponents[i]){attachedComponents[i].destroy()
}}}};
rf.cleanDom=function(source){var e=(typeof source=="string")?document.getElementById(source):$("body").get(0);
if(source=="javax.faces.ViewRoot"){e=$("body").get(0)
}if(e){$(e).trigger("beforeDomClean.RICH");
var elements=e.getElementsByTagName("*");
if(elements.length){$.each(elements,function(index){rf.cleanComponent(this)
});
$.cleanData(elements)
}rf.cleanComponent(e);
$.cleanData([e]);
$(e).trigger("afterDomClean.RICH")
}};
rf.submitForm=function(form,parameters,target){if(typeof form==="string"){form=$(form)
}var initialTarget=form.attr("target");
var parameterInputs=new Array();
try{form.attr("target",target);
if(parameters){for(var parameterName in parameters){var parameterValue=parameters[parameterName];
var input=$("input[name='"+parameterName+"']",form);
if(input.length==0){var newInput=$("<input />").attr({type:"hidden",name:parameterName,value:parameterValue});
if(parameterName==="javax.faces.portletbridge.STATE_ID"){input=newInput.prependTo(form)
}else{input=newInput.appendTo(form)
}}else{input.val(parameterValue)
}input.each(function(){parameterInputs.push(this)
})
}}form.trigger("submit")
}finally{if(initialTarget===undefined){form.removeAttr("target")
}else{form.attr("target",initialTarget)
}$(parameterInputs).remove()
}};
$.fn.toXML=function(){var out="";
if(this.length>0){if(typeof XMLSerializer=="function"||typeof XMLSerializer=="object"){var xs=new XMLSerializer();
this.each(function(){out+=xs.serializeToString(this)
})
}else{if(this[0].xml!==undefined){this.each(function(){out+=this.xml
})
}else{this.each(function(){out+=this
})
}}}return out
};
var CSS_METACHARS_PATTERN=/([#;&,.+*~':"!^$\[\]()=>|\/])/g;
rf.escapeCSSMetachars=function(s){return s.replace(CSS_METACHARS_PATTERN,"\\$1")
};
var logImpl;
rf.setLog=function(newLogImpl){logImpl=newLogImpl
};
rf.log={debug:function(text){if(logImpl){logImpl.debug(text)
}},info:function(text){if(logImpl){logImpl.info(text)
}},warn:function(text){if(logImpl){logImpl.warn(text)
}},error:function(text){if(logImpl){logImpl.error(text)
}},setLevel:function(level){if(logImpl){logImpl.setLevel(level)
}},getLevel:function(){if(logImpl){return logImpl.getLevel()
}return"info"
},clear:function(){if(logImpl){logImpl.clear()
}}};
rf.getValue=function(propertyNamesArray,base){var result=base;
var c=0;
do{result=result[propertyNamesArray[c++]]
}while(result&&c!=propertyNamesArray.length);
return result
};
var VARIABLE_NAME_PATTERN_STRING="[_A-Z,a-z]\\w*";
var VARIABLES_CHAIN=new RegExp("^\\s*"+VARIABLE_NAME_PATTERN_STRING+"(?:\\s*\\.\\s*"+VARIABLE_NAME_PATTERN_STRING+")*\\s*$");
var DOT_SEPARATOR=/\s*\.\s*/;
rf.evalMacro=function(macro,base){var value="";
if(VARIABLES_CHAIN.test(macro)){var propertyNamesArray=$.trim(macro).split(DOT_SEPARATOR);
value=rf.getValue(propertyNamesArray,base);
if(!value){value=rf.getValue(propertyNamesArray,window)
}}else{try{if(base.eval){value=base.eval(macro)
}else{with(base){value=eval(macro)
}}}catch(e){rf.log.warn("Exception: "+e.message+"\n["+macro+"]")
}}if(typeof value=="function"){value=value(base)
}return value||""
};
var ALPHA_NUMERIC_MULTI_CHAR_REGEXP=/^\w+$/;
rf.interpolate=function(placeholders,context){var contextVarsArray=new Array();
for(var contextVar in context){if(ALPHA_NUMERIC_MULTI_CHAR_REGEXP.test(contextVar)){contextVarsArray.push(contextVar)
}}var regexp=new RegExp("\\{("+contextVarsArray.join("|")+")\\}","g");
return placeholders.replace(regexp,function(str,contextVar){return context[contextVar]
})
};
rf.clonePosition=function(element,baseElement,positioning,offset){};
var jsfEventsAdapterEventNames={event:{begin:["begin"],complete:["beforedomupdate"],success:["success","complete"]},error:["error","complete"]};
var getExtensionResponseElement=function(responseXML){return $("partial-response extension#org\\.richfaces\\.extension",responseXML)
};
var JSON_STRING_START=/^\s*(\[|\{)/;
rf.parseJSON=function(dataString){try{if(dataString){if(JSON_STRING_START.test(dataString)){return $.parseJSON(dataString)
}else{var parsedData=$.parseJSON('{"root": '+dataString+"}");
return parsedData.root
}}}catch(e){rf.log.warn("Error evaluating JSON data from element <"+elementName+">: "+e.message)
}return null
};
var getJSONData=function(extensionElement,elementName){var dataString=$.trim(extensionElement.children(elementName).text());
return rf.parseJSON(dataString)
};
rf.createJSFEventsAdapter=function(handlers){var handlers=handlers||{};
var ignoreSuccess;
return function(eventData){var source=eventData.source;
var status=eventData.status;
var type=eventData.type;
if(type=="event"&&status=="begin"){ignoreSuccess=false
}else{if(type=="error"){ignoreSuccess=true
}else{if(ignoreSuccess){return 
}else{if(status=="complete"&&rf.ajaxContainer&&rf.ajaxContainer.isIgnoreResponse&&rf.ajaxContainer.isIgnoreResponse()){return 
}}}}var typeHandlers=jsfEventsAdapterEventNames[type];
var handlerNames=(typeHandlers||{})[status]||typeHandlers;
if(handlerNames){for(var i=0;
i<handlerNames.length;
i++){var eventType=handlerNames[i];
var handler=handlers[eventType];
if(handler){var event={};
$.extend(event,eventData);
event.type=eventType;
if(type!="error"){delete event.status;
if(event.responseXML){var xml=getExtensionResponseElement(event.responseXML);
var data=getJSONData(xml,"data");
var componentData=getJSONData(xml,"componentData");
event.data=data;
event.componentData=componentData||{}
}}handler.call(source,event)
}}}}
};
rf.setGlobalStatusNameVariable=function(statusName){if(statusName){rf.statusName=statusName
}else{delete rf.statusName
}};
rf.setZeroRequestDelay=function(options){if(typeof options.requestDelay=="undefined"){options.requestDelay=0
}};
var chain=function(){var functions=arguments;
if(functions.length==1){return functions[0]
}else{return function(){var callResult;
for(var i=0;
i<functions.length;
i++){var f=functions[i];
if(f){callResult=f.apply(this,arguments)
}}return callResult
}
}};
var createEventHandler=function(handlerCode){if(handlerCode){var safeHandlerCode="try {"+handlerCode+"} catch (e) {window.RichFaces.log.error('Error in method execution: ' + e.message)}";
return new Function("event",safeHandlerCode)
}return null
};
var AJAX_EVENTS=(function(){var serverEventHandler=function(clientHandler,event){var xml=getExtensionResponseElement(event.responseXML);
var serverHandler=createEventHandler(xml.children(event.type).text());
if(clientHandler){clientHandler.call(this,event)
}if(serverHandler){serverHandler.call(this,event)
}};
return{error:null,begin:null,complete:serverEventHandler,beforedomupdate:serverEventHandler}
}());
rf.requestParams=null;
rf.ajax=function(source,event,options){var options=options||{};
var sourceId=getSourceId(source,options);
var sourceElement=getSourceElement(source);
if(sourceElement){source=searchForComponentRootOrReturn(sourceElement)
}parameters=options.parameters||{};
parameters.execute="@component";
parameters.render="@component";
if(options.clientParameters){$.extend(parameters,options.clientParameters)
}if(!parameters["org.richfaces.ajax.component"]){parameters["org.richfaces.ajax.component"]=sourceId
}if(options.incId){parameters[sourceId]=sourceId
}if(rf.queue){parameters.queueId=options.queueId
}var form=getFormElement(sourceElement);
if(window.mojarra&&form&&form.enctype=="multipart/form-data"&&jsf.specversion>20000){var input,name,value;
rf.requestParams=[];
for(var i in parameters){if(parameters.hasOwnProperty(i)){value=parameters[i];
if(i!=="javax.faces.source"&&i!=="javax.faces.partial.event"&&i!=="javax.faces.partial.execute"&&i!=="javax.faces.partial.render"&&i!=="javax.faces.partial.ajax"&&i!=="javax.faces.behavior.event"&&i!=="queueId"){input=document.createElement("input");
input.setAttribute("type","hidden");
input.setAttribute("id",i);
input.setAttribute("name",i);
input.setAttribute("value",value);
form.appendChild(input);
rf.requestParams.push(i)
}}}}parameters.rfExt={};
parameters.rfExt.status=options.status;
for(var eventName in AJAX_EVENTS){parameters.rfExt[eventName]=options[eventName]
}jsf.ajax.request(source,event,parameters)
};
if(window.jsf){jsf.ajax.request=function request(source,event,options){var parameters=$.extend({},options);
parameters.rfExt=null;
var eventHandlers;
var sourceElement=getSourceElement(source);
var form=getFormElement(sourceElement);
for(var eventName in AJAX_EVENTS){var handlerCode,handler;
if(options.rfExt){handlerCode=options.rfExt[eventName];
handler=typeof handlerCode=="function"?handlerCode:createEventHandler(handlerCode)
}var serverHandler=AJAX_EVENTS[eventName];
if(serverHandler){handler=$.proxy(function(clientHandler,event){return serverHandler.call(this,clientHandler,event)
},sourceElement,handler)
}if(handler){eventHandlers=eventHandlers||{};
eventHandlers[eventName]=handler
}}if(options.rfExt&&options.rfExt.status){var namedStatusEventHandler=function(){rf.setGlobalStatusNameVariable(options.rfExt.status)
};
eventHandlers=eventHandlers||{};
if(eventHandlers.begin){eventHandlers.begin=chain(namedStatusEventHandler,eventHandlers.begin)
}else{eventHandlers.begin=namedStatusEventHandler
}}if(form){eventHandlers.begin=chain(eventHandlers.begin,function(){$(form).trigger("ajaxbegin")
});
eventHandlers.beforedomupdate=chain(eventHandlers.beforedomupdate,function(){$(form).trigger("ajaxbeforedomupdate")
});
eventHandlers.complete=chain(eventHandlers.complete,function(){$(form).trigger("ajaxcomplete")
})
}if(eventHandlers){var eventsAdapter=rf.createJSFEventsAdapter(eventHandlers);
parameters.onevent=chain(options.onevent,eventsAdapter);
parameters.onerror=chain(options.onerror,eventsAdapter)
}if(form){$(form).trigger("ajaxsubmit")
}return jsfAjaxRequest(source,event,parameters)
};
jsf.ajax.response=function(request,context){if(context.render=="@component"){context.render=$("extension[id='org.richfaces.extension'] render",request.responseXML).text()
}if(window.mojarra&&rf.requestParams&&rf.requestParams.length){for(var i=0;
i<rf.requestParams.length;
i++){var elements=context.form.childNodes;
for(var j=0;
j<elements.length;
j++){if(!elements[j].type==="hidden"){continue
}if(elements[j].name===rf.requestParams[i]){var node=context.form.removeChild(elements[j]);
node=null;
break
}}}}return jsfAjaxResponse(request,context)
}
}var searchForComponentRootOrReturn=function(sourceElement){if(sourceElement.id&&!isRichFacesComponent(sourceElement)){var parentElement=false;
$(sourceElement).parents().each(function(){if(this.id&&sourceElement.id.indexOf(this.id)==0){var suffix=sourceElement.id.substring(this.id.length);
if(suffix.match(/^[a-zA-Z]*$/)&&isRichFacesComponent(this)){parentElement=this;
return false
}}});
if(parentElement!==false){return parentElement
}}return sourceElement
};
var isRichFacesComponent=function(element){return $(element).data("rf.bridge")||rf.component(element)
};
var getSourceElement=function(source){if(typeof source==="string"){return document.getElementById(source)
}else{if(typeof source==="object"){return source
}else{throw new Error("jsf.request: source must be object or string")
}}};
var getFormElement=function(sourceElement){if($(sourceElement).is("form")){return sourceElement
}else{return $("form").has(sourceElement).get(0)
}};
var getSourceId=function(source,options){if(options.sourceId){return options.sourceId
}else{return(typeof source=="object"&&(source.id||source.name))?(source.id?source.id:source.name):source
}};
var ajaxOnComplete=function(data){var type=data.type;
var responseXML=data.responseXML;
if(data.type=="event"&&data.status=="complete"&&responseXML){var partialResponse=$(responseXML).children("partial-response");
if(partialResponse&&partialResponse.length){var elements=partialResponse.children("changes").children("update, delete");
$.each(elements,function(){rf.cleanDom($(this).attr("id"))
})
}}};
rf.javascriptServiceComplete=function(event){$(function(){$(document).trigger("javascriptServiceComplete")
})
};
var attachAjaxDOMCleaner=function(){if(typeof jsf!="undefined"&&jsf.ajax){jsf.ajax.addOnEvent(ajaxOnComplete);
return true
}return false
};
if(!attachAjaxDOMCleaner()){$(document).ready(attachAjaxDOMCleaner)
}if(window.addEventListener){window.addEventListener("unload",rf.cleanDom,false)
}else{window.attachEvent("onunload",rf.cleanDom)
}rf.browser={};
var ua=navigator.userAgent.toLowerCase(),match=/(chrome)[ \/]([\w.]+)/.exec(ua)||/(webkit)[ \/]([\w.]+)/.exec(ua)||/(opera)(?:.*version|)[ \/]([\w.]+)/.exec(ua)||/(msie) ([\w.]+)/.exec(ua)||/(trident)(?:.*? rv:([\w.]+)|)/.exec(ua)||ua.indexOf("compatible")<0&&/(mozilla)(?:.*? rv:([\w.]+)|)/.exec(ua)||[];
rf.browser[match[1]||""]=true;
rf.browser.version=match[2]||"0";
if(rf.browser.chrome){rf.browser.webkit=true
}else{if(rf.browser.webkit){rf.browser.safari=true
}}if(rf.browser.trident){rf.browser.msie=true
}if(window.myfaces&&myfaces._impl&&myfaces._impl._util&&myfaces._impl._util._Dom.isMultipartCandidate){var oldIsMultipartCandidate=myfaces._impl._util._Dom.isMultipartCandidate,that=myfaces._impl._util._Dom;
myfaces._impl._util._Dom.isMultipartCandidate=function(executes){if(that._Lang.isString(executes)){executes=that._Lang.strToArray(executes,/\s+/)
}for(var cnt=0,len=executes.length;
cnt<len;
cnt++){var element=that.byId(executes[cnt]);
var inputs=that.findByTagName(element,"input",true);
for(var cnt2=0,len2=inputs.length;
cnt2<len2;
cnt2++){if(that.getAttribute(inputs[cnt2],"type")=="file"&&(!that.getAttribute(inputs[cnt2],"class")||that.getAttribute(inputs[cnt2],"class").search("rf-fu-inp")==-1)){return true
}}}return false
}
}}(RichFaces.jQuery,RichFaces));;(function(G,E,A){E.ajaxContainer=E.ajaxContainer||{};
if(E.ajaxContainer.jsfRequest){return 
}E.ajaxContainer.jsfRequest=A.ajax.request;
A.ajax.request=function(J,I,H){E.queue.push(J,I,H)
};
E.ajaxContainer.jsfResponse=A.ajax.response;
E.ajaxContainer.isIgnoreResponse=function(){return E.queue.isIgnoreResponse()
};
A.ajax.response=function(I,H){E.queue.response(I,H)
};
var F="pull";
var D="push";
var C=F;
var B="org.richfaces.queue.global";
E.queue=(function(){var W={};
var Q={};
var H=function(Z,e,d,a){this.queue=Z;
this.source=e;
this.options=G.extend({},a||{});
this.queueOptions={};
var f;
if(this.options.queueId){if(W[this.options.queueId]){f=this.options.queueId
}delete this.options.queueId
}else{var b=E.getDomElement(e);
var c;
if(b){b=G(b).closest("form");
if(b.length>0){c=b.get(0)
}}if(c&&c.id&&W[c.id]){f=c.id
}else{f=B
}}if(f){this.queueOptions=W[f]||{};
if(this.queueOptions.queueId){this.queueOptions=G.extend({},(W[this.queueOptions.queueId]||{}),this.queueOptions)
}else{var b=E.getDomElement(e);
var c;
if(b){b=G(b).closest("form");
if(b.length>0){c=b.get(0)
}}if(c&&c.id&&W[c.id]){f=c.id
}else{f=B
}if(f){this.queueOptions=G.extend({},(W[f]||{}),this.queueOptions)
}}}if(typeof this.queueOptions.requestGroupingId=="undefined"){this.queueOptions.requestGroupingId=typeof this.source=="string"?this.source:this.source.id
}if(d&&d instanceof Object){if("layerX" in d){delete d.layerX
}if("layerY" in d){delete d.layerY
}}this.event=G.extend({},d);
this.requestGroupingId=this.queueOptions.requestGroupingId;
this.eventsCount=1
};
G.extend(H.prototype,{isIgnoreDupResponses:function(){return this.queueOptions.ignoreDupResponses
},getRequestGroupId:function(){return this.requestGroupingId
},setRequestGroupId:function(Z){this.requestGroupingId=Z
},resetRequestGroupId:function(){this.requestGroupingId=undefined
},setReadyToSubmit:function(Z){this.readyToSubmit=Z
},getReadyToSubmit:function(){return this.readyToSubmit
},ondrop:function(){var Z=this.queueOptions.onqueuerequestdrop;
if(Z){Z.call(this.queue,this.source,this.options,this.event)
}},onRequestDelayPassed:function(){this.readyToSubmit=true;
S.call(this.queue)
},startTimer:function(){var Z=this.queueOptions.requestDelay;
if(typeof Z!="number"){Z=this.queueOptions.requestDelay||0
}E.log.debug("Queue will wait "+(Z||0)+"ms before submit");
if(Z){var a=this;
this.timer=window.setTimeout(function(){try{a.onRequestDelayPassed()
}finally{a.timer=undefined;
a=undefined
}},Z)
}else{this.onRequestDelayPassed()
}},stopTimer:function(){if(this.timer){window.clearTimeout(this.timer);
this.timer=undefined
}},clearEntry:function(){this.stopTimer();
if(this.request){this.request.shouldNotifyQueue=false;
this.request=undefined
}},getEventsCount:function(){return this.eventsCount
},setEventsCount:function(Z){this.eventsCount=Z
}});
var V="event";
var L="success";
var K="complete";
var R=[];
var T;
var M=function(b){var a="richfaces.queue: ajax submit error";
if(b){var Z=b.message||b.description;
if(Z){a+=": "+Z
}}E.log.warn(a);
T=null;
S()
};
var O=function(){var b;
var c=false;
while(R.length>0&&!c){b=R[0];
var Z=E.getDomElement(b.source);
if(Z==null||G(Z).closest("form").length==0){var a=R.shift();
a.stopTimer();
E.log.debug("richfaces.queue: removing stale entry from the queue (source element: "+Z+")")
}else{c=true
}}};
var Y=function(Z){if(Z.type==V&&Z.status==L){E.log.debug("richfaces.queue: ajax submit successfull");
T=null;
O();
S()
}};
A.ajax.addOnEvent(Y);
A.ajax.addOnError(M);
var S=function(){if(C==F&&T){E.log.debug("richfaces.queue: Waiting for previous submit results");
return 
}if(P()){E.log.debug("richfaces.queue: Nothing to submit");
return 
}var a;
if(R[0].getReadyToSubmit()){try{a=T=R.shift();
E.log.debug("richfaces.queue: will submit request NOW");
var b=T.options;
b["AJAX:EVENTS_COUNT"]=T.eventsCount;
E.ajaxContainer.jsfRequest(T.source,T.event,b);
if(b.queueonsubmit){b.queueonsubmit.call(a)
}J("onrequestdequeue",a)
}catch(Z){M(Z)
}}};
var P=function(){return(I()==0)
};
var I=function(){return R.length
};
var X=function(){var Z=R.length-1;
return R[Z]
};
var U=function(Z){var a=R.length-1;
R[a]=Z
};
var J=function(a,d){var b=d.queueOptions[a];
if(b){if(typeof (b)=="string"){new Function(b).call(null,d)
}else{b.call(null,d)
}}var c,Z;
if(d.queueOptions.queueId&&(c=W[d.queueOptions.queueId])&&(Z=c[a])&&Z!=b){Z.call(null,d)
}};
var N=function(Z){R.push(Z);
E.log.debug("New request added to queue. Queue requestGroupingId changed to "+Z.getRequestGroupId());
J("onrequestqueue",Z)
};
return{DEFAULT_QUEUE_ID:B,getSize:I,isEmpty:P,submitFirst:function(){if(!P()){var Z=R[0];
Z.stopTimer();
Z.setReadyToSubmit(true);
S()
}},push:function(d,c,a){var b=new H(this,d,c,a);
var e=b.getRequestGroupId();
var Z=X();
if(Z){if(Z.getRequestGroupId()==e){E.log.debug("Similar request currently in queue");
E.log.debug("Combine similar requests and reset timer");
Z.stopTimer();
b.setEventsCount(Z.getEventsCount()+1);
U(b);
J("onrequestqueue",b)
}else{E.log.debug("Last queue entry is not the last anymore. Stopping requestDelay timer and marking entry as ready for submission");
Z.stopTimer();
Z.resetRequestGroupId();
Z.setReadyToSubmit(true);
N(b);
S()
}}else{N(b)
}b.startTimer()
},response:function(a,Z){if(this.isIgnoreResponse()){T=null;
S()
}else{E.ajaxContainer.jsfResponse(a,Z)
}},isIgnoreResponse:function(){var Z=R[0];
return Z&&T.isIgnoreDupResponses()&&T.queueOptions.requestGroupingId==Z.queueOptions.requestGroupingId
},clear:function(){var Z=X();
if(Z){Z.stopTimer()
}R=[]
},setQueueOptions:function(b,Z){var a=typeof b;
if(a=="string"){if(W[b]){throw"Queue already registered"
}else{W[b]=Z
}}else{if(a=="object"){G.extend(W,b)
}}return E.queue
},getQueueOptions:function(Z){return W[Z]||{}
}}
}())
}(RichFaces.jQuery,RichFaces,jsf));;window.RichFaces=window.RichFaces||{};
RichFaces.jQuery=RichFaces.jQuery||window.jQuery;
(function(F,N){N.csv=N.csv||{};
var H={};
var M=/\'?\{(\d+)\}\'?/g;
var B=function(T,Q,R){if(T){var V=T.replace(M,"\n$1\n").split("\n");
var U;
R[9]=Q;
for(var S=1;
S<V.length;
S+=2){U=R[V[S]];
V[S]=typeof U=="undefined"?"":U
}return V.join("")
}else{return""
}};
var G=function(Q){if(null!==Q.value&&undefined!=Q.value){return Q.value
}else{return""
}};
var L=function(Q){if(Q.checked){return true
}else{return false
}};
var P=function(R,Q){if(Q.selected){return R[R.length]=Q.value
}};
var K={hidden:function(Q){return G(Q)
},text:function(Q){return G(Q)
},textarea:function(Q){return G(Q)
},"select-one":function(Q){if(Q.selectedIndex!=-1){return G(Q)
}},password:function(Q){return G(Q)
},file:function(Q){return G(Q)
},radio:function(Q){return L(Q)
},checkbox:function(Q){return L(Q)
},"select-multiple":function(W){var S=W.name;
var V=W.childNodes;
var U=[];
for(var T=0;
T<V.length;
T++){var X=V[T];
if(X.tagName==="OPTGROUP"){var R=X.childNodes;
for(var Q=0;
Q<R.length;
Q++){U=P(U,R[Q])
}}else{U=P(U,X)
}}return U
},input:function(Q){return G(Q)
}};
var E=function(R){var T="";
if(K[R.type]){T=K[R.type](R)
}else{if(undefined!==R.value){T=R.value
}else{var Q=F(R);
if(Q){if(typeof N.component(Q)["getValue"]==="function"){T=N.component(Q).getValue()
}else{var S=F("*",Q).filter(":editable");
if(S){var U=S[0];
T=K[U.type](U)
}}}}}return T
};
var D=function(Q,R){if(Q.p){return Q.p.label||R
}return R
};
F.extend(N.csv,{RE_DIGITS:/^-?\d+$/,RE_FLOAT:/^(-?\d+)?(\.(\d+)?(e[+-]?\d+)?)?$/,addMessage:function(Q){F.extend(H,Q)
},getMessage:function(T,Q,R,S){var U=T?T:H[S]||{detail:"",summary:"",severity:0};
return{detail:B(U.detail,Q,R),summary:B(U.summary,Q,R),severity:U.severity}
},sendMessage:function(Q,R){N.Event.fire(window.document,N.Event.MESSAGE_EVENT_TYPE,{sourceId:Q,message:R})
},clearMessage:function(Q){N.Event.fire(window.document,N.Event.MESSAGE_EVENT_TYPE,{sourceId:Q})
},validate:function(R,T,a,X){var a=N.getDomElement(a||T);
var d=E(a);
var S;
var V=X.c;
N.csv.clearMessage(T);
if(V){var c=D(V,T);
try{if(V.f){S=V.f(d,T,D(V,T),V.m)
}}catch(b){b.severity=2;
N.csv.sendMessage(T,b);
return false
}}else{S=d
}var f=true;
var W=X.v;
var Z;
if(W){var U,Q;
for(var Y=0;
Y<W.length;
Y++){try{Q=W[Y];
U=Q.f;
if(U){U(S,D(Q,T),Q.p,Q.m)
}}catch(b){Z=b;
b.severity=2;
N.csv.sendMessage(T,b);
f=false
}}}if(!f&&X.oninvalid instanceof Function){X.oninvalid([Z])
}if(f){if(!X.da&&X.a){X.a.call(a,R,T)
}else{if(X.onvalid instanceof Function){X.onvalid()
}}}return f
}});
var J=function(W,T,X,U,R,V){var Q=null,S=W;
if(W){W=F.trim(W);
if(!N.csv.RE_DIGITS.test(W)||(Q=parseInt(W,10))<U||Q>R){throw N.csv.getMessage(X,S,V?[W,V,T]:[W,T])
}}return Q
};
var A=function(U,S,V,T){var Q=null,R=U;
if(U){U=F.trim(U);
if(!N.csv.RE_FLOAT.test(U)||isNaN(Q=parseFloat(U))){throw N.csv.getMessage(V,R,T?[U,T,S]:[U,S])
}}return Q
};
F.extend(N.csv,{convertBoolean:function(S,Q,U,T){if(typeof S==="string"){var R=F.trim(S).toLowerCase();
if(R==="on"||R==="true"||R==="yes"){return true
}}else{if(true===S){return true
}}return false
},convertDate:function(S,R,U,T){var Q;
S=F.trim(S);
Q=Date.parse(S);
return Q
},convertByte:function(R,Q,T,S){return J(R,Q,S,-128,127,254)
},convertNumber:function(T,S,V,U){var Q,R=T;
T=F.trim(T);
Q=parseFloat(T);
if(isNaN(Q)){throw N.csv.getMessage(U,R,[T,99,S])
}return Q
},convertFloat:function(R,Q,T,S){return A(R,Q,S,2000000000)
},convertDouble:function(R,Q,T,S){return A(R,Q,S,1999999)
},convertShort:function(R,Q,T,S){return J(R,Q,S,-32768,32767,32456)
},convertInteger:function(R,Q,T,S){return J(R,Q,S,-2147483648,2147483648,9346)
},convertCharacter:function(R,Q,T,S){return J(R,Q,S,0,65535)
},convertLong:function(R,Q,T,S){return J(R,Q,S,-9223372036854776000,9223372036854776000,98765432)
}});
var O=function(Q,S,R,W,V){var U=typeof W.min==="number";
var T=typeof W.max==="number";
if(T&&S>W.max){throw N.csv.getMessage(V,Q,U?[W.min,W.max,R]:[W.max,R])
}if(U&&S<W.min){throw N.csv.getMessage(V,Q,T?[W.min,W.max,R]:[W.min,R])
}};
var C=function(U,Q,T,W){if(typeof T!="string"||T.length==0){throw N.csv.getMessage(W,U,[],"REGEX_VALIDATOR_PATTERN_NOT_SET")
}var S=I(T);
var R;
try{R=new RegExp(S)
}catch(V){throw N.csv.getMessage(W,U,[],"REGEX_VALIDATOR_MATCH_EXCEPTION")
}if(!R.test(U)){throw N.csv.getMessage(W,U,[T,Q])
}};
var I=function(Q){if(!(Q.slice(0,1)==="^")){Q="^"+Q
}if(!(Q.slice(-1)==="$")){Q=Q+"$"
}return Q
};
F.extend(N.csv,{validateLongRange:function(T,R,V,U){var S=typeof T,Q=T;
if(S!=="number"){if(S!="string"){throw N.csv.getMessage(U,T,[componentId,""],"LONG_RANGE_VALIDATOR_TYPE")
}else{T=F.trim(T);
if(!N.csv.RE_DIGITS.test(T)||(T=parseInt(T,10))==NaN){throw N.csv.getMessage(U,T,[componentId,""],"LONG_RANGE_VALIDATOR_TYPE")
}}}O(Q,T,R,V,U)
},validateDoubleRange:function(T,R,V,U){var S=typeof T,Q=T;
if(S!=="number"){if(S!=="string"){throw N.csv.getMessage(U,T,[componentId,""],"DOUBLE_RANGE_VALIDATOR_TYPE")
}else{T=F.trim(T);
if(!N.csv.RE_FLOAT.test(T)||(T=parseFloat(T))==NaN){throw N.csv.getMessage(U,T,[componentId,""],"DOUBLE_RANGE_VALIDATOR_TYPE")
}}}O(Q,T,R,V,U)
},validateLength:function(S,Q,U,T){var R=S?S.length:0;
O(S,R,Q,U,T)
},validateSize:function(S,Q,U,T){var R=S?S.length:0;
O(S,R,Q,U,T)
},validateRegex:function(R,Q,T,S){C(R,Q,T.pattern,S)
},validatePattern:function(R,Q,T,S){C(R,Q,T.regexp,S)
},validateRequired:function(R,Q,T,S){if(undefined===R||null===R||""===R){throw N.csv.getMessage(S,R,[Q])
}},validateTrue:function(R,Q,T,S){if(R!==true){throw S
}},validateFalse:function(R,Q,T,S){if(R!==false){throw S
}},validateMax:function(R,Q,T,S){if(R>T.value){throw S
}},validateMin:function(R,Q,T,S){if(R<T.value){throw S
}}})
})(RichFaces.jQuery,RichFaces);;window.RichFaces=window.RichFaces||{};
RichFaces.jQuery=RichFaces.jQuery||window.jQuery;
(function(C,B,D){B.blankFunction=function(){};
B.BaseComponent=function(F){this.id=F;
this.options=this.options||{}
};
var A={};
var E=function(H,L,G){G=G||{};
var J=B.blankFunction;
J.prototype=H.prototype;
L.prototype=new J();
L.prototype.constructor=L;
L.$super=H.prototype;
if(L.$super==B.BaseComponent.prototype){var I=jQuery.extend({},A,G||{})
}var K=L;
L.extend=function(F,M){M=M||{};
var N=jQuery.extend({},I||G||{},M||{});
return E(K,F,N)
};
return I||G
};
B.BaseComponent.extend=function(G,F){return E(B.BaseComponent,G,F)
};
B.BaseComponent.extendClass=function(G){var F=G.init||B.blankFunction;
var H=this;
H.extend(F);
F.extendClass=H.extendClass;
C.extend(F.prototype,G);
return F
};
C.extend(B.BaseComponent.prototype,(function(F){return{name:"BaseComponent",toString:function(){var G=[];
if(this.constructor.$super){G[G.length]=this.constructor.$super.toString()
}G[G.length]=this.name;
return G.join(", ")
},getValue:function(){return 
},getEventElement:function(){return this.id
},attachToDom:function(I){I=I||this.id;
var H=B.getDomElement(I);
if(H){var G=H[B.RICH_CONTAINER]=H[B.RICH_CONTAINER]||{};
G.component=this
}return H
},detach:function(H){H=H||this.id;
var G=B.getDomElement(H);
G&&G[B.RICH_CONTAINER]&&(G[B.RICH_CONTAINER].component=null)
},invokeEvent:function(J,I,L,N){var K,G;
var M=C.extend({},L,{type:J});
if(!M){if(document.createEventObject){M=document.createEventObject();
M.type=J
}else{if(document.createEvent){M=document.createEvent("Events");
M.initEvent(J,true,false)
}}}M[B.RICH_CONTAINER]={component:this,data:N};
var H=this.options["on"+J];
if(typeof H=="function"){K=H.call(I,M)
}if(B.Event){G=B.Event.callHandler(this,J,N)
}if(G!=false&&K!=false){G=true
}return G
},destroy:function(){}}
})(D));
B.BaseNonVisualComponent=function(F){this.id=F;
this.options=this.options||{}
};
B.BaseNonVisualComponent.extend=function(G,F){return E(B.BaseNonVisualComponent,G,F)
};
B.BaseNonVisualComponent.extendClass=function(G){var F=G.init||B.blankFunction;
var H=this;
H.extend(F);
F.extendClass=H.extendClass;
C.extend(F.prototype,G);
return F
};
C.extend(B.BaseNonVisualComponent.prototype,(function(F){return{name:"BaseNonVisualComponent",toString:function(){var G=[];
if(this.constructor.$super){G[G.length]=this.constructor.$super.toString()
}G[G.length]=this.name;
return G.join(", ")
},getValue:function(){return 
},attachToDom:function(I){I=I||this.id;
var H=B.getDomElement(I);
if(H){var G=H[B.RICH_CONTAINER]=H[B.RICH_CONTAINER]||{};
if(G.attachedComponents){G.attachedComponents[this.name]=this
}else{G.attachedComponents={};
G.attachedComponents[this.name]=this
}}return H
},detach:function(H){H=H||this.id;
var G=B.getDomElement(H);
G&&G[B.RICH_CONTAINER]&&(G[B.RICH_CONTAINER].attachedComponents[this.name]=null)
},destroy:function(){}}
})(D))
})(jQuery,window.RichFaces||(window.RichFaces={}));
(function(B,A){A.ui=A.ui||{};
A.ui.Base=function(F,E,D){this.namespace="."+A.Event.createNamespace(this.name,F);
C.constructor.call(this,F);
this.options=B.extend(this.options,D,E);
this.attachToDom();
this.__bindEventHandlers()
};
A.BaseComponent.extend(A.ui.Base);
var C=A.ui.Base.$super;
B.extend(A.ui.Base.prototype,{__bindEventHandlers:function(){},destroy:function(){A.Event.unbindById(this.id,this.namespace);
C.destroy.call(this)
}})
})(RichFaces.jQuery,RichFaces);;(function(E){E.fn.setPosition=function(Q,R){var M=typeof Q;
if(M=="object"||M=="string"){var O={};
if(M=="string"||Q.nodeType||Q instanceof jQuery||typeof Q.length!="undefined"){O=H(Q)
}else{if(Q.type){O=C(Q)
}else{if(Q.id){O=H(document.getElementById(Q.id))
}else{O=Q
}}}var R=R||{};
var P=R.type||R.from||R.to?E.PositionTypes[R.type||G]:{noPositionType:true};
var N=E.extend({},D,P,R);
if(!N.noPositionType){if(N.from.length>2){N.from=B[N.from.toLowerCase()]
}if(N.to.length>2){N.to=B[N.to.toLowerCase()]
}}return this.each(function(){element=E(this);
F(O,element,N)
})
}return this
};
var G="TOOLTIP";
var D={collision:"",offset:[0,0]};
var K=/^(left|right)-(top|buttom|auto)$/i;
var B={"top-left":"LT","top-right":"RT","bottom-left":"LB","bottom-right":"RB","top-auto":"AT","bottom-auto":"AB","auto-left":"LA","auto-right":"RA","auto-auto":"AA"};
E.PositionTypes={TOOLTIP:{from:"AA",to:"AA",auto:["RTRT","RBRT","LTRT","RTLT","LTLT","LBLT","RTRB","RBRB","LBRB","RBLB"]},DROPDOWN:{from:"AA",to:"AA",auto:["LBRB","LTRT","RBLB","RTLT"]},DDMENUGROUP:{from:"AA",to:"AA",auto:["RTRB","RBRT","LTLB","LBLT"]}};
E.addPositionType=function(N,M){E.PositionTypes[N]=M
};
function C(M){var N=E.event.fix(M);
return{width:0,height:0,left:N.pageX,top:N.pageY}
}function H(P){var N=E(P);
var O=N.offset();
var T={width:N.outerWidth(),height:N.outerHeight(),left:Math.floor(O.left),top:Math.floor(O.top)};
if(N.length>1){var M,U,O;
var R;
for(var Q=1;
Q<N.length;
Q++){R=N.eq(Q);
if(R.css("display")=="none"){continue
}M=R.outerWidth();
U=R.outerHeight();
O=R.offset();
var S=T.left-O.left;
if(S<0){if(M-S>T.width){T.width=M-S
}}else{T.width+=S
}var S=T.top-O.top;
if(S<0){if(U-S>T.height){T.height=U-S
}}else{T.height+=S
}if(O.left<T.left){T.left=O.left
}if(O.top<T.top){T.top=O.top
}}}return T
}function J(M,N){if(M.left>=N.left&&M.top>=N.top&&M.right<=N.right&&M.bottom<=N.bottom){return 0
}var O={left:(M.left>N.left?M.left:N.left),top:(M.top>N.top?M.top:N.top)};
O.right=M.right<N.right?(M.right==M.left?O.left:M.right):N.right;
O.bottom=M.bottom<N.bottom?(M.bottom==M.top?O.top:M.bottom):N.bottom;
return(O.right-O.left)*(O.bottom-O.top)
}function A(Q,O,M,R){var P={};
var N=R.charAt(0);
if(N=="L"){P.left=Q.left
}else{if(N=="R"){P.left=Q.left+Q.width
}}N=R.charAt(1);
if(N=="T"){P.top=Q.top
}else{if(N=="B"){P.top=Q.top+Q.height
}}N=R.charAt(2);
if(N=="L"){P.left-=O[0];
P.right=P.left;
P.left-=M.width
}else{if(N=="R"){P.left+=O[0];
P.right=P.left+M.width
}}N=R.charAt(3);
if(N=="T"){P.top-=O[1];
P.bottom=P.top;
P.top-=M.height
}else{if(N=="B"){P.top+=O[1];
P.bottom=P.top+M.height
}}return P
}function I(O,N){var M="";
var P;
while(M.length<O.length){P=O.charAt(M.length);
M+=P=="A"?N.charAt(M.length):P
}return M
}function L(T,O,R,X,Z){var W={square:0};
var V;
var Y;
var P,N;
var M=Z.from+Z.to;
if(M.indexOf("A")<0){return A(T,O,X,M)
}else{var S=M=="AAAA";
var U;
for(var Q=0;
Q<Z.auto.length;
Q++){U=S?Z.auto[Q]:I(M,Z.auto[Q]);
V=A(T,O,X,U);
P=V.left;
N=V.top;
Y=J(V,R);
if(Y!=0){if(P>=0&&N>=0&&W.square<Y){W={x:P,y:N,square:Y}
}}else{break
}}if(Y!=0&&(P<0||N<0||W.square>Y)){P=W.x;
N=W.y
}}return{left:P,top:N}
}function F(X,R,Z){var O=R.width();
var Y=R.height();
X.width=X.width||0;
X.height=X.height||0;
var Q=parseInt(R.css("left"),10);
if(isNaN(Q)||Q==0){Q=0;
R.css("left","0px")
}if(isNaN(X.left)){X.left=Q
}var W=parseInt(R.css("top"),10);
if(isNaN(W)||W==0){W=0;
R.css("top","0px")
}if(isNaN(X.top)){X.top=W
}var V={};
if(Z.noPositionType){V.left=X.left+X.width+Z.offset[0];
V.top=X.top+Z.offset[1]
}else{var S=E(window);
var P={left:S.scrollLeft(),top:S.scrollTop()};
P.right=P.left+S.width();
P.bottom=P.top+S.height();
V=L(X,Z.offset,P,{width:O,height:Y},Z)
}var N=false;
var U;
var T;
if(R.css("display")=="none"){N=true;
T=R.get(0);
U=T.style.visibility;
T.style.visibility="hidden";
T.style.display="block"
}var M=R.offset();
if(N){T.style.visibility=U;
T.style.display="none"
}V.left+=Q-Math.floor(M.left);
V.top+=W-Math.floor(M.top);
if(Q!=V.left){R.css("left",(V.left+"px"))
}if(W!=V.top){R.css("top",(V.top+"px"))
}}})(jQuery);;(function(F,D){var C=["debug","info","warn","error"];
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
}(RichFaces.jQuery,RichFaces));;(function($){var undefined,dataFlag="watermark",dataClass="watermarkClass",dataFocus="watermarkFocus",dataFormSubmit="watermarkSubmit",dataMaxLen="watermarkMaxLength",dataPassword="watermarkPassword",dataText="watermarkText",selWatermarkDefined=":data("+dataFlag+")",selWatermarkAble=":text,:password,:search,textarea",triggerFns=["Page_ClientValidate"],pageDirty=false;
$.extend($.expr[":"],{search:function(elem){return"search"===(elem.type||"")
},data:function(element,index,matches,set){var data,parts=/^((?:[^=!^$*]|[!^$*](?!=))+)(?:([!^$*]?=)(.*))?$/.exec(matches[3]);
if(parts){data=$(element).data(parts[1]);
if(data!==undefined){if(parts[2]){data=""+data;
switch(parts[2]){case"=":return(data==parts[3]);
case"!=":return(data!=parts[3]);
case"^=":return(data.slice(0,parts[3].length)==parts[3]);
case"$=":return(data.slice(-parts[3].length)==parts[3]);
case"*=":return(data.indexOf(parts[3])!==-1)
}}return true
}}return false
}});
$.watermark={version:"3.0.6",options:{className:"watermark",useNative:true},hide:function(selector){$(selector).filter(selWatermarkDefined).each(function(){$.watermark._hide($(this))
})
},_hide:function($input,focus){var inputVal=$input.val()||"",inputWm=$input.data(dataText)||"",maxLen=$input.data(dataMaxLen)||0,className=$input.data(dataClass);
if((inputWm.length)&&(inputVal==inputWm)){$input.val("");
if($input.data(dataPassword)){if(($input.attr("type")||"")==="text"){var $pwd=$input.data(dataPassword)||[],$wrap=$input.parent()||[];
if(($pwd.length)&&($wrap.length)){$wrap[0].removeChild($input[0]);
$wrap[0].appendChild($pwd[0]);
$input=$pwd
}}}if(maxLen){$input.attr("maxLength",maxLen);
$input.removeData(dataMaxLen)
}if(focus){$input.attr("autocomplete","off");
window.setTimeout(function(){$input.select()
},1)
}}className&&$input.removeClass(className)
},show:function(selector){$(selector).filter(selWatermarkDefined).each(function(){$.watermark._show($(this))
})
},_show:function($input){var val=$input.val()||"",text=$input.data(dataText)||"",type=$input.attr("type")||"",className=$input.data(dataClass);
if(((val.length==0)||(val==text))&&(!$input.data(dataFocus))){pageDirty=true;
if($input.data(dataPassword)){if(type==="password"){var $pwd=$input.data(dataPassword)||[],$wrap=$input.parent()||[];
if(($pwd.length)&&($wrap.length)){$wrap[0].removeChild($input[0]);
$wrap[0].appendChild($pwd[0]);
$input=$pwd;
$input.attr("maxLength",text.length)
}}}if((type==="text")||(type==="search")){var maxLen=$input.attr("maxLength")||0;
if((maxLen>0)&&(text.length>maxLen)){$input.data(dataMaxLen,maxLen);
$input.attr("maxLength",text.length)
}}className&&$input.addClass(className);
$input.val(text)
}else{$.watermark._hide($input)
}},hideAll:function(){if(pageDirty){$.watermark.hide(selWatermarkAble);
pageDirty=false
}},showAll:function(){$.watermark.show(selWatermarkAble)
}};
$.fn.watermark=function(text,options){if(!this.length){return this
}var hasClass=false,hasText=(typeof (text)==="string");
if(typeof (options)==="object"){hasClass=(typeof (options.className)==="string");
options=$.extend({},$.watermark.options,options)
}else{if(typeof (options)==="string"){hasClass=true;
options=$.extend({},$.watermark.options,{className:options})
}else{options=$.watermark.options
}}if(typeof (options.useNative)!=="function"){options.useNative=options.useNative?function(){return true
}:function(){return false
}
}return this.each(function(){var $input=$(this);
if(!$input.is(selWatermarkAble)){return 
}if($input.data(dataFlag)){if(hasText||hasClass){$.watermark._hide($input);
if(hasText){$input.data(dataText,text)
}if(hasClass){$input.data(dataClass,options.className)
}}}else{if(options.useNative.call(this,$input)){if(((""+$input.css("-webkit-appearance")).replace("undefined","")!=="")&&((($input.attr("tagName")||"")!=="TEXTAREA"))&&$input.size()>0&&$input[0].tagName!=="TEXTAREA"){if(hasText){$input.attr("placeholder",text)
}return 
}}$input.data(dataText,hasText?text:"");
$input.data(dataClass,options.className);
$input.data(dataFlag,1);
if(($input.attr("type")||"")==="password"){var $wrap=$input.wrap("<span>").parent(),$wm=$($wrap.html().replace(/type=["']?password["']?/i,'type="text"'));
$wm.data(dataText,$input.data(dataText));
$wm.data(dataClass,$input.data(dataClass));
$wm.data(dataFlag,1);
$wm.attr("maxLength",text.length);
$wm.focus(function(){$.watermark._hide($wm,true)
}).bind("dragenter",function(){$.watermark._hide($wm)
}).bind("dragend",function(){window.setTimeout(function(){$wm.blur()
},1)
});
$input.blur(function(){$.watermark._show($input)
}).bind("dragleave",function(){$.watermark._show($input)
});
$wm.data(dataPassword,$input);
$input.data(dataPassword,$wm)
}else{$input.focus(function(){$input.data(dataFocus,1);
$.watermark._hide($input,true)
}).blur(function(){$input.data(dataFocus,0);
$.watermark._show($input)
}).bind("dragenter",function(){$.watermark._hide($input)
}).bind("dragleave",function(){$.watermark._show($input)
}).bind("dragend",function(){window.setTimeout(function(){$.watermark._show($input)
},1)
}).bind("drop",function(evt){var dropText=evt.originalEvent.dataTransfer.getData("Text");
if($input.val().replace(dropText,"")===$input.data(dataText)){$input.val(dropText)
}$input.focus()
})
}if(this.form){var form=this.form,$form=$(form);
if(!$form.data(dataFormSubmit)){$form.submit($.watermark.hideAll);
if(form.submit){$form.data(dataFormSubmit,form.onsubmit||1);
form.onsubmit=(function(f,$f){return function(){var nativeSubmit=$f.data(dataFormSubmit);
$.watermark.hideAll();
if(nativeSubmit instanceof Function){nativeSubmit()
}else{eval(nativeSubmit)
}}
})(form,$form)
}else{$form.data(dataFormSubmit,1);
form.submit=(function(f){return function(){$.watermark.hideAll();
delete f.submit;
f.submit()
}
})(form)
}}}}$.watermark._show($input)
})
};
if(triggerFns.length){$(function(){var i,name,fn;
for(i=triggerFns.length-1;
i>=0;
i--){name=triggerFns[i];
fn=window[name];
if(typeof (fn)==="function"){window[name]=(function(origFn){return function(){$.watermark.hideAll();
return origFn.apply(null,Array.prototype.slice.call(arguments))
}
})(fn)
}}})
}})(jQuery);;(function(F,E){var D=function(){return E.statusName
};
var A="richfaces:ajaxStatus";
var G=function(H){return H?(A+"@"+H):A
};
var C=function(O,S){if(S){var N=D();
var H=O.source;
var R=false;
var J=G(N);
var I;
if(N){I=[F(document)]
}else{I=[F(H).parents("form"),F(document)]
}for(var P=0;
P<I.length&&!R;
P++){var L=I[P];
var K=L.data(J);
if(K){for(var Q in K){var M=K[Q];
var T=M[S].apply(M,arguments);
if(T){R=true
}else{delete K[Q]
}}if(!R){L.removeData(J)
}}}}};
var B=function(){var H=arguments.callee;
if(!H.initialized){H.initialized=true;
var I=E.createJSFEventsAdapter({begin:function(J){C(J,"start")
},error:function(J){C(J,"error")
},success:function(J){C(J,"success")
},complete:function(){E.setGlobalStatusNameVariable(null)
}});
jsf.ajax.addOnEvent(I);
jsf.ajax.addOnError(I)
}};
E.ui=E.ui||{};
E.ui.Status=E.BaseComponent.extendClass({name:"Status",init:function(I,H){this.id=I;
this.attachToDom();
this.options=H||{};
this.register()
},register:function(){B();
var J=this.options.statusName;
var H=G(J);
var I;
if(J){I=F(document)
}else{I=F(E.getDomElement(this.id)).parents("form");
if(I.length==0){I=F(document)
}}var K=I.data(H);
if(!K){K={};
I.data(H,K)
}K[this.id]=this
},start:function(){if(this.options.onstart){this.options.onstart.apply(this,arguments)
}return this.__showHide(".rf-st-start")
},stop:function(){this.__stop();
return this.__showHide(".rf-st-stop")
},success:function(){if(this.options.onsuccess){this.options.onsuccess.apply(this,arguments)
}return this.stop()
},error:function(){if(this.options.onerror){this.options.onerror.apply(this,arguments)
}this.__stop();
return this.__showHide(":not(.rf-st-error) + .rf-st-stop, .rf-st-error")
},__showHide:function(H){var I=F(E.getDomElement(this.id));
if(I){var J=I.children();
J.each(function(){var K=F(this);
K.css("display",K.is(H)?"":"none")
});
return true
}return false
},__stop:function(){if(this.options.onstop){this.options.onstop.apply(this,arguments)
}}})
}(RichFaces.jQuery,window.RichFaces));;if(!window.RichFaces){window.RichFaces={}
}(function($,rf){rf.ui=rf.ui||{};
var evaluate=function(selector){var result=selector;
try{result=eval(selector)
}catch(e){}return result
};
var evaluateJQuery=function(element,selector){var result=element||evaluate(selector);
if(!(result instanceof $)){result=$(result||"")
}return result
};
var createEventHandlerFunction=function(opts){var newFunction=new Function("event",opts.query);
return function(){var selector=evaluateJQuery(null,opts.selector);
if(opts.attachType!="live"){selector[opts.attachType||"bind"](opts.event,null,newFunction)
}else{$(document).on(opts.event,selector.selector,null,newFunction)
}}
};
var createDirectQueryFunction=function(opts){var queryFunction=new Function("options","arguments[1]."+opts.query);
return function(){var element;
var options;
if(arguments.length==1){if(!opts.selector){element=arguments[0]
}else{options=arguments[0]
}}else{element=arguments[0];
options=arguments[1]
}var selector=evaluateJQuery(element,opts.selector);
queryFunction.call(this,options,selector)
}
};
var createQueryFunction=function(options){if(options.event){return createEventHandlerFunction(options)
}else{return createDirectQueryFunction(options)
}};
var query=function(options){if(options.timing=="immediate"){createQueryFunction(options).call(this)
}else{$(document).ready(createQueryFunction(options))
}};
rf.ui.jQueryComponent={createFunction:createQueryFunction,query:query}
}(RichFaces.jQuery,RichFaces));;(function(C,B){B.ui=B.ui||{};
var A={};
B.ui.Poll=function(F,E){D.constructor.call(this,F,E);
this.id=F;
this.attachToDom();
this.interval=E.interval||1000;
this.ontimer=E.ontimer;
this.pollElement=B.getDomElement(this.id);
B.ui.pollTracker=B.ui.pollTracker||{};
if(E.enabled){this.startPoll()
}};
B.BaseComponent.extend(B.ui.Poll);
var D=B.ui.Poll.$super;
C.extend(B.ui.Poll.prototype,(function(){return{name:"Poll",startPoll:function(){this.stopPoll();
var E=this;
B.ui.pollTracker[E.id]=window.setTimeout(function(){try{E.ontimer.call(E.pollElement||window);
E.startPoll()
}catch(F){}},E.interval)
},stopPoll:function(){if(B.ui.pollTracker&&B.ui.pollTracker[this.id]){window.clearTimeout(B.ui.pollTracker[this.id]);
delete B.ui.pollTracker[this.id]
}},setZeroRequestDelay:function(E){if(typeof E.requestDelay=="undefined"){E.requestDelay=0
}},destroy:function(){this.stopPoll();
this.detach(this.id);
D.destroy.call(this)
}}
})())
})(RichFaces.jQuery,RichFaces);;(function(B,A){A.ui=A.ui||{};
A.ui.CollapsibleSubTable=function(H,G,F){this.id=H;
this.options=B.extend(this.options,F||{});
this.stateInput=F.stateInput;
this.optionsInput=F.optionsInput;
this.expandMode=F.expandMode||A.ui.CollapsibleSubTable.MODE_CLNT;
this.eventOptions=F.eventOptions;
this.formId=G;
this.isNested=F.isNested;
if(!this.isNested){var D=this;
var E=B(document.getElementById(this.id)).parent();
E.find(".rf-dt-c-srt").each(function(){B(this).bind("click",{sortHandle:this},B.proxy(D.sortHandler,D))
});
E.find(".rf-dt-flt-i").each(function(){B(this).bind("blur",{filterHandle:this},B.proxy(D.filterHandler,D))
})
}this.attachToDom()
};
B.extend(A.ui.CollapsibleSubTable,{MODE_AJAX:"ajax",MODE_SRV:"server",MODE_CLNT:"client",collapse:0,expand:1,SORTING:"rich:sorting",FILTERING:"rich:filtering"});
A.BaseComponent.extend(A.ui.CollapsibleSubTable);
var C=A.ui.CollapsibleSubTable.$super;
B.extend(A.ui.CollapsibleSubTable.prototype,(function(){var E=function(M,L){A.ajax(this.id,M,{parameters:L})
};
var I=function(P,R,N,M){var Q={};
var O=this.id+P;
Q[O]=(R+":"+(N||"")+":"+M);
var L=this.options.ajaxEventOption;
for(O in L){if(!Q[O]){Q[O]=L[O]
}}return Q
};
var F=function(){if(!this.isNested){return B(document.getElementById(this.id)).parent()
}else{var L=new RegExp("^"+this.id+"\\:\\d+\\:b$");
return B(document.getElementById(this.id)).parent().find("tr").filter(function(){return this.id.match(L)
})
}};
var G=function(){return B(document.getElementById(this.stateInput))
};
var K=function(){return B(document.getElementById(this.optionsInput))
};
var H=function(M,L){this.__switchState();
A.ajax(this.id,M,L)
};
var J=function(L){this.__switchState();
B(document.getElementById(this.formId)).submit()
};
var D=function(L){if(this.isExpanded()){this.collapse(L)
}else{this.expand(L)
}};
return{name:"CollapsibleSubTable",sort:function(M,N,L){E.call(this,null,I.call(this,A.ui.CollapsibleSubTable.SORTING,M,N,L))
},clearSorting:function(){this.sort("","",true)
},sortHandler:function(O){var L=B(O.data.sortHandle);
var M=L.find(".rf-dt-srt-btn");
var P=M.data("columnid");
var N=M.hasClass("rf-dt-srt-asc")?"descending":"ascending";
this.sort(P,N,false)
},filter:function(M,N,L){E.call(this,null,I.call(this,A.ui.CollapsibleSubTable.FILTERING,M,N,L))
},clearFiltering:function(){this.filter("","",true)
},filterHandler:function(M){var L=B(M.data.filterHandle);
var N=L.data("columnid");
var O=L.val();
this.filter(N,O,false)
},switchState:function(M,L){if(this.expandMode==A.ui.CollapsibleSubTable.MODE_AJAX){H.call(this,M,this.eventOptions,L)
}else{if(this.expandMode==A.ui.CollapsibleSubTable.MODE_SRV){J.call(this,L)
}else{if(this.expandMode==A.ui.CollapsibleSubTable.MODE_CLNT){D.call(this,L)
}}}},collapse:function(L){if(this.isNested){var M=new RegExp("^"+this.id+"\\:\\d+\\:\\w+\\:expanded$");
var O=new RegExp("^"+this.id+"\\:\\d+\\:\\w+\\:collapsed$");
var N=new RegExp("^"+this.id+"\\:\\d+\\:\\w+$");
B(document.getElementById(this.id)).parent().find("tr[style='display: none;']").filter(function(){return this.id.match(N)
}).each(function(){if(this.rf){if(this.rf.component.isExpanded){B(document.getElementById(this.id)).parent().find(".rf-csttg-exp").filter(function(){return this.id.match(M)
}).each(function(){B(this).hide()
});
B(document.getElementById(this.id)).parent().find(".rf-csttg-colps").filter(function(){return this.id.match(O)
}).each(function(){B(this).show()
});
this.rf.component.collapse()
}}})
}this.setState(A.ui.CollapsibleSubTable.collapse);
F.call(this).hide()
},expand:function(L){this.setState(A.ui.CollapsibleSubTable.expand);
F.call(this).show()
},isExpanded:function(){return(parseInt(this.getState())==A.ui.CollapsibleSubTable.expand)
},__switchState:function(L){var M=this.isExpanded()?A.ui.CollapsibleSubTable.collapse:A.ui.CollapsibleSubTable.expand;
this.setState(M)
},getState:function(){return G.call(this).val()
},setState:function(L){G.call(this).val(L)
},setOption:function(L){K.call(this).val(L)
},getMode:function(){return this.expandMode
},destroy:function(){C.destroy.call(this)
}}
})())
})(RichFaces.jQuery,window.RichFaces);;(function(B,A){A.ui=A.ui||{};
A.ui.DataTable=function(G,E){C.constructor.call(this,G);
this.options=B.extend(this.options,E||{});
this.element=this.attachToDom();
var D=this;
var F=B(this.element).find(".rf-dt-thd");
F.find(".rf-dt-c-srt").each(function(){B(this).bind("click",{sortHandle:this},B.proxy(D.sortHandler,D))
});
F.find(".rf-dt-flt-i").each(function(){B(this).bind("blur",{filterHandle:this},B.proxy(D.filterHandler,D))
});
B(this.element).trigger("rich:ready",this)
};
A.BaseComponent.extend(A.ui.DataTable);
var C=A.ui.DataTable.$super;
B.extend(A.ui.DataTable,{SORTING:"rich:sorting",FILTERING:"rich:filtering",SUBTABLE_SELECTOR:".rf-cst"});
B.extend(A.ui.DataTable.prototype,(function(){var D=function(G,F){A.ajax(this.id,G,{parameters:F})
};
var E=function(J,L,H,G){var K={};
var I=this.id+J;
K[I]=(L+":"+(H||"")+":"+G);
var F=this.options.ajaxEventOption;
for(I in F){if(!K[I]){K[I]=F[I]
}}return K
};
return{name:"RichFaces.ui.DataTable",sort:function(G,H,F){D.call(this,null,E.call(this,A.ui.DataTable.SORTING,G,H,F))
},clearSorting:function(){this.sort("","",true)
},sortHandler:function(I){var F=B(I.data.sortHandle);
var G=F.find(".rf-dt-srt-btn");
var J=G.data("columnid");
var H=G.hasClass("rf-dt-srt-asc")?"descending":"ascending";
this.sort(J,H,false)
},filter:function(G,H,F){D.call(this,null,E.call(this,A.ui.DataTable.FILTERING,G,H,F))
},clearFiltering:function(){this.filter("","",true)
},filterHandler:function(G){var F=B(G.data.filterHandle);
var H=F.data("columnid");
var I=F.val();
this.filter(H,I,false)
},expandAllSubTables:function(){this.invokeOnSubTables("expand")
},collapseAllSubTables:function(){this.invokeOnSubTables("collapse")
},switchSubTable:function(F){this.getSubTable(F).switchState()
},getSubTable:function(F){return A.component(F)
},invokeOnSubTables:function(G){var F=B(document.getElementById(this.id)).children(A.ui.DataTable.SUBTABLE_SELECTOR);
var H=this.invokeOnComponent;
F.each(function(){if(this.firstChild&&this.firstChild[A.RICH_CONTAINER]&&this.firstChild[A.RICH_CONTAINER].component){var I=this.firstChild[A.RICH_CONTAINER].component;
if(I instanceof RichFaces.ui.CollapsibleSubTable){H(I,G)
}}})
},invokeOnSubTable:function(H,G){var F=this.getSubTable(H);
this.invokeOnComponent(F,G)
},invokeOnComponent:function(F,H){if(F){var G=F[H];
if(typeof G=="function"){G.call(F)
}}},contextMenuAttach:function(G){var F="[id='"+this.element.id+"'] ";
F+=(typeof G.options.targetSelector==="undefined")?".rf-dt-b td":G.options.targetSelector;
F=B.trim(F);
A.Event.bind(F,G.options.showEvent,B.proxy(G.__showHandler,G),G)
},destroy:function(){C.destroy.call(this)
}}
})())
})(RichFaces.jQuery,window.RichFaces);;(function(C,B){B.ui=B.ui||{};
B.ui.DragIndicator=function(F,E){D.constructor.call(this,F);
this.attachToDom(F);
this.indicator=C(document.getElementById(F));
this.options=E
};
var A={};
B.BaseComponent.extend(B.ui.DragIndicator);
var D=B.ui.DragIndicator.$super;
C.extend(B.ui.DragIndicator.prototype,(function(){return{show:function(){this.indicator.show()
},hide:function(){this.indicator.hide()
},getAcceptClass:function(){return this.options.acceptClass
},getRejectClass:function(){return this.options.rejectClass
},getDraggingClass:function(){return this.options.draggingClass
},getElement:function(){return this.indicator
}}
})())
})(RichFaces.jQuery,window.RichFaces);;(function(B,A){A.ui=A.ui||{};
A.ui.toolbarHandlers=function(E){if(E.id&&E.events){B(".rf-tb-itm",document.getElementById(E.id)).bind(E.events)
}var C=E.groups;
if(C&&C.length>0){var H;
var F;
for(F in C){H=C[F];
if(H){var D=H.ids;
var I;
var G=[];
for(I in D){G.push(document.getElementById(D[I]))
}B(G).bind(H.events)
}}}}
})(RichFaces.jQuery,RichFaces);;window.RichFaces=window.RichFaces||{};
RichFaces.jQuery=RichFaces.jQuery||window.jQuery;
(function(E,C){C.Event=C.Event||{};
var B=function(F){if(!F){throw"RichFaces.Event: empty selector"
}var G;
if(C.BaseComponent&&F instanceof C.BaseComponent){G=E(C.getDomElement(F.getEventElement()))
}else{G=E(F)
}return G
};
var D=function(F,G){return function(H,I){if(!H[C.RICH_CONTAINER]){H[C.RICH_CONTAINER]={data:I}
}return G.call(F||this,H,this,I)
}
};
var A=function(H,G){var F={};
for(var I in H){F[I]=D(G,H[I])
}return F
};
E.extend(C.Event,{RICH_NAMESPACE:"RICH",EVENT_NAMESPACE_SEPARATOR:".",MESSAGE_EVENT_TYPE:"onmessage",ready:function(F){return E(document).ready(F)
},bind:function(F,H,I,G,K){if(typeof H=="object"){B(F).bind(A(H,I),K)
}else{var J=D(G,I);
B(F).bind(H,K,J);
return J
}},bindById:function(K,G,H,F,J){if(typeof G=="object"){E(document.getElementById(K)).bind(A(G,H),J)
}else{var I=D(F,H);
E(document.getElementById(K)).bind(G,J,I)
}return I
},bindOne:function(F,H,I,G,K){var J=D(G,I);
B(F).one(H,K,J);
return J
},bindOneById:function(K,G,H,F,J){var I=D(F,H);
E(document.getElementById(K)).one(G,J,I);
return I
},unbind:function(F,G,H){return B(F).unbind(G,H)
},unbindById:function(H,F,G){return E(document.getElementById(H)).unbind(F,G)
},bindScrollEventHandlers:function(G,H,F){var I=[];
G=C.getDomElement(G).parentNode;
while(G&&G!=window.document.body){if(G.offsetWidth!=G.scrollWidth||G.offsetHeight!=G.scrollHeight){I.push(G);
C.Event.bind(G,"scroll"+F.getNamespace(),H,F)
}G=G.parentNode
}return I
},unbindScrollEventHandlers:function(G,F){C.Event.unbind(G,"scroll"+F.getNamespace())
},fire:function(F,G,I){var H=E.Event(G);
B(F).trigger(H,[I]);
return !H.isDefaultPrevented()
},fireById:function(I,F,H){var G=E.Event(F);
E(document.getElementById(I)).trigger(G,[H]);
return !G.isDefaultPrevented()
},callHandler:function(F,G,H){return B(F).triggerHandler(G,[H])
},callHandlerById:function(H,F,G){return E(document.getElementById(H)).triggerHandler(F,[G])
},createNamespace:function(G,I,H){var F=[];
F.push(H||C.Event.RICH_NAMESPACE);
if(G){F.push(G)
}if(I){F.push(I)
}return F.join(C.Event.EVENT_NAMESPACE_SEPARATOR)
}})
})(RichFaces.jQuery,RichFaces);;(function(C,B){B.ui=B.ui||{};
var A={useNative:false};
B.ui.Placeholder=B.BaseComponent.extendClass({name:"Placeholder",init:function(F,E){D.constructor.call(this,F);
E=C.extend({},A,E);
this.attachToDom(this.id);
C(function(){E.className="rf-plhdr "+((E.styleClass)?E.styleClass:"");
var H=(E.selector)?C(E.selector):C(document.getElementById(E.targetId));
var G=H.find("*").andSelf().filter(":editable");
G.watermark(E.text,E)
})
},destroy:function(){D.destroy.call(this)
}});
C(function(){C(document).on("ajaxsubmit","form",C.watermark.hideAll);
C(document).on("ajaxbegin","form",C.watermark.showAll);
C(document).on("reset","form",function(){setTimeout(C.watermark.showAll,0)
})
});
var D=B.ui.Placeholder.$super
})(RichFaces.jQuery,RichFaces);;/* Copyright (c) 2013 Brandon Aaron (http://brandon.aaron.sh)
 * Licensed under the MIT License (LICENSE.txt).
 *
 * Version: 3.1.12
 *
 * Requires: jQuery 1.2.2+
 */
(function(A){if(typeof define==="function"&&define.amd){define(["jquery"],A)
}else{if(typeof exports==="object"){module.exports=A
}else{A(jQuery)
}}}(function(C){var D=["wheel","mousewheel","DOMMouseScroll","MozMousePixelScroll"],J=("onwheel" in document||document.documentMode>=9)?["wheel"]:["mousewheel","DomMouseScroll","MozMousePixelScroll"],H=Array.prototype.slice,I,B;
if(C.event.fixHooks){for(var E=D.length;
E;
){C.event.fixHooks[D[--E]]=C.event.mouseHooks
}}var F=C.event.special.mousewheel={version:"3.1.12",setup:function(){if(this.addEventListener){for(var L=J.length;
L;
){this.addEventListener(J[--L],K,false)
}}else{this.onmousewheel=K
}C.data(this,"mousewheel-line-height",F.getLineHeight(this));
C.data(this,"mousewheel-page-height",F.getPageHeight(this))
},teardown:function(){if(this.removeEventListener){for(var L=J.length;
L;
){this.removeEventListener(J[--L],K,false)
}}else{this.onmousewheel=null
}C.removeData(this,"mousewheel-line-height");
C.removeData(this,"mousewheel-page-height")
},getLineHeight:function(M){var L=C(M),N=L["offsetParent" in C.fn?"offsetParent":"parent"]();
if(!N.length){N=C("body")
}return parseInt(N.css("fontSize"),10)||parseInt(L.css("fontSize"),10)||16
},getPageHeight:function(L){return C(L).height()
},settings:{adjustOldDeltas:true,normalizeOffset:true}};
C.fn.extend({mousewheel:function(L){return L?this.bind("mousewheel",L):this.trigger("mousewheel")
},unmousewheel:function(L){return this.unbind("mousewheel",L)
}});
function K(L){var O=L||window.event,U=H.call(arguments,1),W=0,Q=0,P=0,T=0,S=0,R=0;
L=C.event.fix(O);
L.type="mousewheel";
if("detail" in O){P=O.detail*-1
}if("wheelDelta" in O){P=O.wheelDelta
}if("wheelDeltaY" in O){P=O.wheelDeltaY
}if("wheelDeltaX" in O){Q=O.wheelDeltaX*-1
}if("axis" in O&&O.axis===O.HORIZONTAL_AXIS){Q=P*-1;
P=0
}W=P===0?Q:P;
if("deltaY" in O){P=O.deltaY*-1;
W=P
}if("deltaX" in O){Q=O.deltaX;
if(P===0){W=Q*-1
}}if(P===0&&Q===0){return 
}if(O.deltaMode===1){var V=C.data(this,"mousewheel-line-height");
W*=V;
P*=V;
Q*=V
}else{if(O.deltaMode===2){var N=C.data(this,"mousewheel-page-height");
W*=N;
P*=N;
Q*=N
}}T=Math.max(Math.abs(P),Math.abs(Q));
if(!B||T<B){B=T;
if(A(O,T)){B/=40
}}if(A(O,T)){W/=40;
Q/=40;
P/=40
}W=Math[W>=1?"floor":"ceil"](W/B);
Q=Math[Q>=1?"floor":"ceil"](Q/B);
P=Math[P>=1?"floor":"ceil"](P/B);
if(F.settings.normalizeOffset&&this.getBoundingClientRect){var M=this.getBoundingClientRect();
S=L.clientX-M.left;
R=L.clientY-M.top
}L.deltaX=Q;
L.deltaY=P;
L.deltaFactor=B;
L.offsetX=S;
L.offsetY=R;
L.deltaMode=0;
U.unshift(L,W,Q,P);
if(I){clearTimeout(I)
}I=setTimeout(G,200);
return(C.event.dispatch||C.event.handle).apply(this,U)
}function G(){B=null
}function A(M,L){return F.settings.adjustOldDeltas&&M.type==="mousewheel"&&L%120===0
}}));;(function(F,C){C.utils=C.utils||{};
C.utils.Cache=function(K,J,I,H){this.key=K.toLowerCase();
this.cache={};
this.cache[this.key]=J||[];
this.originalValues=typeof I=="function"?I(J):I||this.cache[this.key];
this.values=D(this.originalValues);
this.useCache=H||B.call(this)
};
var D=function(H){var J=[];
for(var I=0;
I<H.length;
I++){J.push(H[I].toLowerCase())
}return J
};
var B=function(){var H=true;
for(var I=0;
I<this.values.length;
I++){if(this.values[I].indexOf(this.key)!=0){H=false;
break
}}return H
};
var G=function(J,O){J=J.toLowerCase();
var H=[];
if(J.length<this.key.length){return H
}if(this.cache[J]){H=this.cache[J]
}else{var K=typeof O=="function";
var M=this.cache[this.key];
for(var I=0;
I<this.values.length;
I++){var L=this.values[I];
if(K&&O(J,L)){H.push(M[I])
}else{var N=L.indexOf(J);
if(N==0){H.push(M[I])
}}}if((!this.lastKey||J.indexOf(this.lastKey)!=0)&&H.length>0){this.cache[J]=H;
if(H.length==1){this.lastKey=J
}}}return H
};
var E=function(H){return this.originalValues[this.cache[this.key].index(H)]
};
var A=function(H){H=H.toLowerCase();
return this.cache[H]||this.useCache&&H.indexOf(this.key)==0
};
F.extend(C.utils.Cache.prototype,(function(){return{getItems:G,getItemValue:E,isCached:A}
})())
})(RichFaces.jQuery,RichFaces);;(function(C,B){B.ui=B.ui||{};
function A(E){this.comp=E
}A.prototype={exec:function(F,E){if(E.switchMode=="server"){return this.execServer(F,E)
}else{if(E.switchMode=="ajax"){return this.execAjax(F,E)
}else{if(E.switchMode=="client"){return this.execClient(F,E)
}else{B.log.error("SwitchItems.exec : unknown switchMode ("+this.comp.switchMode+")")
}}}},execServer:function(G,E){if(G){var F=G.__leave();
if(!F){return false
}}this.__setActiveItem(E.getName());
B.submitForm(this.__getParentForm());
return false
},execAjax:function(G,E){var F=C.extend({},this.comp.options.ajax,{});
this.__setActiveItem(E.getName());
B.ajax(this.comp.id,null,F);
if(G){this.__setActiveItem(G.getName())
}return false
},execClient:function(G,E){if(G){var F=G.__leave();
if(!F){return false
}}this.__setActiveItem(E.getName());
E.__enter();
this.comp.__fireItemChange(G,E);
return true
},__getParentForm:function(){return C(B.getDomElement(this.comp.id)).parents("form:first")
},__setActiveItem:function(E){B.getDomElement(this.__getValueInputId()).value=E;
this.comp.activeItem=E
},__getValueInputId:function(){return this.comp.id+"-value"
}};
B.ui.TogglePanel=B.BaseComponent.extendClass({name:"TogglePanel",init:function(F,E){D.constructor.call(this,F);
this.attachToDom();
this.items=[];
this.options=C.extend(this.options,E||{});
this.activeItem=this.options.activeItem;
this.__addUserEventHandler("itemchange");
this.__addUserEventHandler("beforeitemchange")
},getSelectItem:function(){return this.activeItem
},switchToItem:function(F){var E=this.getNextItem(F);
if(E==null){B.log.warn("TogglePanel.switchToItems("+F+"): item with name '"+F+"' not found");
return false
}var H=this.__getItemByName(this.getSelectItem());
var G=this.__fireBeforeItemChange(H,E);
if(!G){B.log.warn("TogglePanel.switchToItems("+F+"): switch has been canceled by beforeItemChange event");
return false
}return this.__itemsSwitcher().exec(H,E)
},getNextItem:function(F){if(F){var E=this.__ITEMS_META_NAMES[F];
if(E){return this.__getItem(E(this))
}else{return this.__getItemByName(F)
}}else{return this.__getItemByName(this.nextItem())
}},onCompleteHandler:function(E){var G=this.__getItemByName(this.activeItem);
var F=this.__getItemByName(E);
this.__itemsSwitcher().execClient(G,F);
C(document.getElementById(F.getTogglePanel().id)).trigger("resize")
},getItems:function(){return this.items
},getItemsNames:function(){var F=[];
for(var E=0;
E<this.items.length;
E++){F.push(this.items[E].getName())
}return F
},nextItem:function(F){var E=this.__getItemIndex(F||this.activeItem);
if(E==-1){return null
}return this.__getItemName(E+1)
},firstItem:function(){return this.__getItemName(0)
},lastItem:function(){return this.__getItemName(this.items.length-1)
},prevItem:function(F){var E=this.__getItemIndex(F||this.activeItem);
if(!this.options.cycledSwitching&&E<1){return null
}return this.__getItemName(E-1)
},__itemsSwitcher:function(){return new A(this)
},__ITEMS_META_NAMES:(function(){function E(F,I,H){var G=I;
while((!F.items[G]||F.items[G].disabled)&&G<F.items.length&&G>0){G+=H
}return G
}return{"@first":function(F){return E(F,0,1)
},"@prev":function(F){return E(F,parseInt(F.__getItemIndex(F.activeItem))-1,-1)
},"@next":function(F){return E(F,parseInt(F.__getItemIndex(F.activeItem))+1,1)
},"@last":function(F){return E(F,F.items.length-1,-1)
}}
})(),__getItemIndex:function(G){var F;
for(var E=0;
E<this.items.length;
E++){F=this.items[E];
if(!F.disabled&&F.getName()===G){return E
}}B.log.info("TogglePanel.getItemIndex: item with name '"+G+"' not found");
return -1
},__addUserEventHandler:function(E){var F=this.options["on"+E];
if(F){B.Event.bindById(this.id,E,F)
}},__getItem:function(E){if(this.options.cycledSwitching){var F=this.items.length;
return this.items[(F+E)%F]
}else{if(E>=0&&E<this.items.length){return this.items[E]
}else{return null
}}},__getItemByName:function(E){return this.__getItem(this.__getItemIndex(E))
},__getItemName:function(E){var F=this.__getItem(E);
if(F==null){return null
}return F.getName()
},__fireItemChange:function(F,E){return new B.Event.fireById(this.id,"itemchange",{id:this.id,oldItem:F,newItem:E})
},__fireBeforeItemChange:function(F,E){return B.Event.fireById(this.id,"beforeitemchange",{id:this.id,oldItem:F,newItem:E})
}});
var D=B.ui.TogglePanel.$super
})(RichFaces.jQuery,RichFaces);;/*
 * jQuery UI Core 1.11.2
 * http://jqueryui.com
 *
 * Copyright 2014 jQuery Foundation and other contributors
 * Released under the MIT license.
 * http://jquery.org/license
 *
 * http://api.jqueryui.com/category/ui-core/
 */
(function(A){if(typeof define==="function"&&define.amd){define(["jquery"],A)
}else{A(jQuery)
}}(function(A){A.ui=A.ui||{};
A.extend(A.ui,{version:"1.11.2",keyCode:{BACKSPACE:8,COMMA:188,DELETE:46,DOWN:40,END:35,ENTER:13,ESCAPE:27,HOME:36,LEFT:37,PAGE_DOWN:34,PAGE_UP:33,PERIOD:190,RIGHT:39,SPACE:32,TAB:9,UP:38}});
A.fn.extend({scrollParent:function(F){var E=this.css("position"),D=E==="absolute",G=F?/(auto|scroll|hidden)/:/(auto|scroll)/,H=this.parents().filter(function(){var I=A(this);
if(D&&I.css("position")==="static"){return false
}return G.test(I.css("overflow")+I.css("overflow-y")+I.css("overflow-x"))
}).eq(0);
return E==="fixed"||!H.length?A(this[0].ownerDocument||document):H
},uniqueId:(function(){var D=0;
return function(){return this.each(function(){if(!this.id){this.id="ui-id-"+(++D)
}})
}
})(),removeUniqueId:function(){return this.each(function(){if(/^ui-id-\d+$/.test(this.id)){A(this).removeAttr("id")
}})
}});
function C(F,D){var H,G,E,I=F.nodeName.toLowerCase();
if("area"===I){H=F.parentNode;
G=H.name;
if(!F.href||!G||H.nodeName.toLowerCase()!=="map"){return false
}E=A("img[usemap='#"+G+"']")[0];
return !!E&&B(E)
}return(/input|select|textarea|button|object/.test(I)?!F.disabled:"a"===I?F.href||D:D)&&B(F)
}function B(D){return A.expr.filters.visible(D)&&!A(D).parents().addBack().filter(function(){return A.css(this,"visibility")==="hidden"
}).length
}A.extend(A.expr[":"],{data:A.expr.createPseudo?A.expr.createPseudo(function(D){return function(E){return !!A.data(E,D)
}
}):function(F,E,D){return !!A.data(F,D[3])
},focusable:function(D){return C(D,!isNaN(A.attr(D,"tabindex")))
},tabbable:function(F){var D=A.attr(F,"tabindex"),E=isNaN(D);
return(E||D>=0)&&C(F,!E)
}});
if(!A("<a>").outerWidth(1).jquery){A.each(["Width","Height"],function(F,D){var E=D==="Width"?["Left","Right"]:["Top","Bottom"],G=D.toLowerCase(),I={innerWidth:A.fn.innerWidth,innerHeight:A.fn.innerHeight,outerWidth:A.fn.outerWidth,outerHeight:A.fn.outerHeight};
function H(L,K,J,M){A.each(E,function(){K-=parseFloat(A.css(L,"padding"+this))||0;
if(J){K-=parseFloat(A.css(L,"border"+this+"Width"))||0
}if(M){K-=parseFloat(A.css(L,"margin"+this))||0
}});
return K
}A.fn["inner"+D]=function(J){if(J===undefined){return I["inner"+D].call(this)
}return this.each(function(){A(this).css(G,H(this,J)+"px")
})
};
A.fn["outer"+D]=function(J,K){if(typeof J!=="number"){return I["outer"+D].call(this,J)
}return this.each(function(){A(this).css(G,H(this,J,true,K)+"px")
})
}
})
}if(!A.fn.addBack){A.fn.addBack=function(D){return this.add(D==null?this.prevObject:this.prevObject.filter(D))
}
}if(A("<a>").data("a-b","a").removeData("a-b").data("a-b")){A.fn.removeData=(function(D){return function(E){if(arguments.length){return D.call(this,A.camelCase(E))
}else{return D.call(this)
}}
})(A.fn.removeData)
}A.ui.ie=!!/msie [\w.]+/.exec(navigator.userAgent.toLowerCase());
A.fn.extend({focus:(function(D){return function(E,F){return typeof E==="number"?this.each(function(){var G=this;
setTimeout(function(){A(G).focus();
if(F){F.call(G)
}},E)
}):D.apply(this,arguments)
}
})(A.fn.focus),disableSelection:(function(){var D="onselectstart" in document.createElement("div")?"selectstart":"mousedown";
return function(){return this.bind(D+".ui-disableSelection",function(E){E.preventDefault()
})
}
})(),enableSelection:function(){return this.unbind(".ui-disableSelection")
},zIndex:function(G){if(G!==undefined){return this.css("zIndex",G)
}if(this.length){var E=A(this[0]),D,F;
while(E.length&&E[0]!==document){D=E.css("position");
if(D==="absolute"||D==="relative"||D==="fixed"){F=parseInt(E.css("zIndex"),10);
if(!isNaN(F)&&F!==0){return F
}}E=E.parent()
}}return 0
}});
A.ui.plugin={add:function(E,F,H){var D,G=A.ui[E].prototype;
for(D in H){G.plugins[D]=G.plugins[D]||[];
G.plugins[D].push([F,H[D]])
}},call:function(D,G,F,E){var H,I=D.plugins[G];
if(!I){return 
}if(!E&&(!D.element[0].parentNode||D.element[0].parentNode.nodeType===11)){return 
}for(H=0;
H<I.length;
H++){if(D.options[I[H][0]]){I[H][1].apply(D.element,F)
}}}}
}));;(function(B,A){A.ui=A.ui||{};
A.ui.CollapsibleSubTableToggler=function(D,C){this.id=D;
this.eventName=C.eventName;
this.expandedControl=C.expandedControl;
this.collapsedControl=C.collapsedControl;
this.forId=C.forId;
this.element=B(document.getElementById(this.id));
if(this.element&&this.eventName){this.element.bind(this.eventName,B.proxy(this.switchState,this))
}};
B.extend(A.ui.CollapsibleSubTableToggler.prototype,(function(){var C=function(D){return B(document.getElementById(D))
};
return{switchState:function(E){var D=A.component(this.forId);
if(D){var F=D.getMode();
if(A.ui.CollapsibleSubTable.MODE_CLNT==F){this.toggleControl(D.isExpanded())
}D.setOption(this.id);
D.switchState(E)
}},toggleControl:function(F){var D=C(this.expandedControl);
var E=C(this.collapsedControl);
if(F){D.hide();
E.show()
}else{E.hide();
D.show()
}}}
})())
})(RichFaces.jQuery,window.RichFaces);;JSNode=function(){};
JSNode.prototype={tag:null,attrs:{},childs:[],value:"",_symbols:{"&":"&amp;","<":"&lt;",">":"&gt;",'"':"&quot;","'":"&apos;","\u00A0":"&nbsp;"},getInnerHTML:function(F){var B=[];
for(var A=0;
A<this.childs.length;
A++){B.push(this.childs[A].getContent(F))
}return B.join("")
},xmlEscape:function(A){return RichFaces.jQuery("<div></div>").text(A).html()
}};
E=function(F,A,B){this.tag=F;
if(A){this.attrs=A
}if(B){this.childs=B
}};
E.prototype=new JSNode();
E.prototype.getContent=function(G){var F="<"+this.tag;
var A=this.getInnerHTML(G);
if(A==""){this.isEmpty=true
}else{this.isEmpty=false
}for(var B in this.attrs){if(!this.attrs.hasOwnProperty(B)){continue
}var H=this.attrs[B];
if(typeof H=="function"){H=H.call(this,G)
}if(H){F+=" "+(B=="className"?"class":B)+'="'+this.xmlEscape(H)+'"'
}}F+=">"+A+"</"+this.tag+">";
return F
};
ET=function(A){this.value=A
};
ET.prototype.getContent=function(A){var B=this.value;
if(typeof B=="function"){B=B(A)
}if(B&&B.getContent){B=B.getContent(A)
}if(B){return B
}return""
};
T=function(A){this.value=A
};
T.prototype=new JSNode();
T.prototype.getContent=function(A){var B=this.value;
if(typeof B=="function"){B=B(A)
}if(B){return this.xmlEscape(B)
}return""
};
C=function(A){this.value=A
};
C.prototype.getContent=function(A){return"<!--"+this.value+"-->"
};
D=function(A){this.value=A
};
D.prototype.getContent=function(A){return"<![CDATA["+this.value+"]]>"
};;(function(E,L){var D="__NEW_NODE_TOGGLE_STATE";
var C="__TRIGGER_NODE_AJAX_UPDATE";
var K="__SELECTION_STATE";
var I=["rf-tr-nd-colps","rf-tr-nd-exp"];
var A=["rf-trn-hnd-colps","rf-trn-hnd-exp"];
var B=["rf-trn-ico-colps","rf-trn-ico-exp"];
L.ui=L.ui||{};
L.ui.TreeNode=L.BaseComponent.extendClass({name:"TreeNode",init:function(P,O){G.constructor.call(this,P);
this.__rootElt=E(this.attachToDom());
this.__children=new Array();
this.__initializeChildren(O);
var N=(O.clientEventHandlers||{})[this.getId().substring(O.treeId.length)]||{};
if(N.bth){L.Event.bind(this.__rootElt,"beforetoggle",new Function("event",N.bth))
}if(N.th){L.Event.bind(this.__rootElt,"toggle",new Function("event",N.th))
}this.__addLastNodeClass()
},destroy:function(){if(this.parent){this.parent.removeChild(this);
this.parent=null
}this.__clientToggleStateInput=null;
this.__clearChildren();
this.__rootElt=null;
G.destroy.call(this)
},__initializeChildren:function(N){var O=this;
this.__rootElt.children(".rf-tr-nd").each(function(){O.addChild(new L.ui.TreeNode(this,N))
})
},__addLastNodeClass:function(){if(this.__rootElt.next("div").length==0){this.__rootElt.addClass("rf-tr-nd-last")
}},__getNodeContainer:function(){return this.__rootElt.find(" > .rf-trn:first")
},__getHandle:function(){return this.__getNodeContainer().find(" > .rf-trn-hnd:first")
},__getContent:function(){return this.__getNodeContainer().find(" > .rf-trn-cnt:first")
},__getIcons:function(){return this.__getContent().find(" > .rf-trn-ico")
},getParent:function(){return this.__parent
},setParent:function(N){this.__parent=N
},addChild:function(P,N){var O;
if(typeof N!="undefined"){O=N
}else{O=this.__children.length
}this.__children.splice(O,0,P);
P.setParent(this)
},removeChild:function(Q){if(this.__children.length){var N=this.__children.indexOf(Q);
if(N!=-1){var O=this.__children.splice(N,1);
if(O){for(var P=0;
P<O.length;
P++){O[P].setParent(undefined)
}}}}},__clearChildren:function(){for(var N=0;
N<this.__children.length;
N++){this.__children[N].setParent(undefined)
}this.__children=new Array()
},isExpanded:function(){return !this.isLeaf()&&this.__rootElt.hasClass("rf-tr-nd-exp")
},isCollapsed:function(){return !this.isLeaf()&&this.__rootElt.hasClass("rf-tr-nd-colps")
},isLeaf:function(){return this.__rootElt.hasClass("rf-tr-nd-lf")
},__canBeToggled:function(){return !this.isLeaf()&&!this.__rootElt.hasClass("rf-tr-nd-exp-nc")&&!this.__loading
},toggle:function(){if(!this.__canBeToggled()){return 
}if(this.isCollapsed()){this.expand()
}else{this.collapse()
}},__updateClientToggleStateInput:function(N){if(!this.__clientToggleStateInput){this.__clientToggleStateInput=E("<input type='hidden' />").appendTo(this.__rootElt).attr({name:this.getId()+D})
}this.__clientToggleStateInput.val(N.toString())
},__fireBeforeToggleEvent:function(){return L.Event.callHandler(this.__rootElt,"beforetoggle")
},__fireToggleEvent:function(){L.Event.callHandler(this.__rootElt,"toggle")
},__makeLoading:function(){this.__loading=true;
this.__getNodeContainer().addClass("rf-trn-ldn")
},__resetLoading:function(){this.__loading=false;
this.__getNodeContainer().removeClass("rf-trn-ldn")
},__changeToggleState:function(P){if(!this.isLeaf()){if(P^this.isExpanded()){if(this.__fireBeforeToggleEvent()===false){return 
}var N=this.getTree();
switch(N.getToggleType()){case"client":this.__rootElt.addClass(I[P?1:0]).removeClass(I[!P?1:0]);
this.__getHandle().addClass(A[P?1:0]).removeClass(A[!P?1:0]);
var O=this.__getIcons();
if(O.length==1){O.addClass(B[P?1:0]).removeClass(B[!P?1:0])
}this.__updateClientToggleStateInput(P);
this.__fireToggleEvent();
break;
case"ajax":case"server":N.__sendToggleRequest(null,this,P);
break
}}}},collapse:function(){this.__changeToggleState(false)
},expand:function(){this.__changeToggleState(true)
},__setSelected:function(O){var N=this.__getContent();
if(O){N.addClass("rf-trn-sel")
}else{N.removeClass("rf-trn-sel")
}this.__selected=O
},isSelected:function(){return this.__selected
},getTree:function(){return this.getParent().getTree()
},getId:function(){return this.__rootElt.attr("id")
}});
var G=L.ui.TreeNode.$super;
L.ui.TreeNode.initNodeByAjax=function(O,Q){var P=E(document.getElementById(O));
var N=Q||{};
var T=P.parent(".rf-tr-nd, .rf-tr");
var U=P.prevAll(".rf-tr-nd").length;
var R=L.component(T[0]);
N.treeId=R.getTree().getId();
var S=new L.ui.TreeNode(P[0],N);
R.addChild(S,U);
var V=R.getTree();
if(V.getSelection().contains(S.getId())){S.__setSelected(true)
}};
L.ui.TreeNode.emitToggleEvent=function(O){var N=document.getElementById(O);
if(!N){return 
}L.component(N).__fireToggleEvent()
};
var M=function(N){return L.component(E(N).closest(".rf-tr"))
};
var J=function(N){return L.component(E(N).closest(".rf-tr-nd"))
};
var F=function(N,O){return N!=M(O)
};
L.ui.Tree=L.ui.TreeNode.extendClass({name:"Tree",init:function(P,N){this.__treeRootElt=E(L.getDomElement(P));
var O={};
O.clientEventHandlers=N.clientEventHandlers||{};
O.treeId=P;
H.constructor.call(this,this.__treeRootElt,O);
this.__toggleType=N.toggleType||"ajax";
this.__selectionType=N.selectionType||"client";
if(N.ajaxSubmitFunction){this.__ajaxSubmitFunction=new Function("event","source","params","complete",N.ajaxSubmitFunction)
}if(N.onbeforeselectionchange){L.Event.bind(this.__treeRootElt,"beforeselectionchange",new Function("event",N.onbeforeselectionchange))
}if(N.onselectionchange){L.Event.bind(this.__treeRootElt,"selectionchange",new Function("event",N.onselectionchange))
}this.__toggleNodeEvent=N.toggleNodeEvent;
if(this.__toggleNodeEvent){this.__treeRootElt.delegate(".rf-trn",this.__toggleNodeEvent,this,this.__nodeToggleActivated)
}if(!this.__toggleNodeEvent||this.__toggleNodeEvent!="click"){this.__treeRootElt.delegate(".rf-trn-hnd","click",this,this.__nodeToggleActivated)
}this.__treeRootElt.delegate(".rf-trn-cnt","mousedown",this,this.__nodeSelectionActivated);
this.__findSelectionInput();
this.__selection=new L.ui.TreeNodeSet(this.__selectionInput.val());
E(document).ready(E.proxy(this.__updateSelectionFromInput,this))
},__findSelectionInput:function(){this.__selectionInput=E(" > .rf-tr-sel-inp",this.__treeRootElt)
},__addLastNodeClass:function(){},destroy:function(){if(this.__toggleNodeEvent){this.__treeRootElt.undelegate(".rf-trn",this.__toggleNodeEvent,this,this.__nodeToggleActivated)
}if(!this.__toggleNodeEvent||this.__toggleNodeEvent!="click"){this.__treeRootElt.undelegate(".rf-trn-hnd","click",this,this.__nodeToggleActivated)
}this.__treeRootElt.undelegate(".rf-trn-cnt","mousedown",this.__nodeSelectionActivated);
this.__treeRootElt=null;
this.__selectionInput=null;
this.__ajaxSubmitFunction=null;
H.destroy.call(this)
},__nodeToggleActivated:function(O){var N=O.data;
if(F(N,this)){return 
}var P=J(this);
P.toggle()
},__nodeSelectionActivated:function(O){var N=O.data;
if(F(N,this)){return 
}var P=J(this);
if(O.ctrlKey){N.__toggleSelection(P)
}else{N.__addToSelection(P)
}},__sendToggleRequest:function(R,O,S){var P=O.getId();
var N={};
N[P+D]=S;
if(this.getToggleType()=="server"){var Q=this.__treeRootElt.closest("form");
L.submitForm(Q,N)
}else{O.__makeLoading();
N[P+C]=S;
this.__ajaxSubmitFunction(R,P,N,function(){var T=L.component(P);
if(T){T.__resetLoading()
}})
}},getToggleType:function(){return this.__toggleType
},getSelectionType:function(){return this.__selectionType
},getTree:function(){return this
},__handleSelectionChange:function(N){var O={oldSelection:this.getSelection().getNodes(),newSelection:N.getNodes()};
if(L.Event.callHandler(this.__treeRootElt,"beforeselectionchange",O)===false){return 
}this.__selectionInput.val(N.getNodeString());
if(this.getSelectionType()=="client"){this.__updateSelection(N)
}else{this.__ajaxSubmitFunction(null,this.getId())
}},__toggleSelection:function(O){var N=this.getSelection().cloneAndToggle(O);
this.__handleSelectionChange(N)
},__addToSelection:function(O){var N=this.getSelection().cloneAndAdd(O);
this.__handleSelectionChange(N)
},__updateSelectionFromInput:function(){this.__findSelectionInput();
this.__updateSelection(new L.ui.TreeNodeSet(this.__selectionInput.val()))
},__updateSelection:function(N){var O=this.getSelection();
O.each(function(){this.__setSelected(false)
});
N.each(function(){this.__setSelected(true)
});
if(O.getNodeString()!=N.getNodeString()){L.Event.callHandler(this.__treeRootElt,"selectionchange",{oldSelection:O.getNodes(),newSelection:N.getNodes()})
}this.__selection=N
},getSelection:function(){return this.__selection
},contextMenuAttach:function(O){var N="[id='"+this.id[0].id+"'] ";
N+=(typeof O.options.targetSelector==="undefined")?".rf-trn-cnt":O.options.targetSelector;
N=E.trim(N);
L.Event.bind(N,O.options.showEvent,E.proxy(O.__showHandler,O),O)
}});
var H=L.ui.Tree.$super;
L.ui.TreeNodeSet=function(){this.init.apply(this,arguments)
};
E.extend(L.ui.TreeNodeSet.prototype,{init:function(N){this.__nodeId=N
},contains:function(N){if(N.getId){return this.__nodeId==N.getId()
}else{return this.__nodeId==N
}},getNodeString:function(){return this.__nodeId
},toString:function(){return this.getNodeString()
},getNodes:function(){if(this.__nodeId){var N=L.component(this.__nodeId);
if(N){return[N]
}else{return null
}}return[]
},cloneAndAdd:function(N){return new L.ui.TreeNodeSet(N.getId())
},cloneAndToggle:function(N){var O;
if(this.contains(N)){O=""
}else{O=N.getId()
}return new L.ui.TreeNodeSet(O)
},each:function(N){E.each(this.getNodes()||[],N)
}})
}(RichFaces.jQuery,RichFaces));;(function(C,B){B.ui=B.ui||{};
var A={expandSingle:true,bubbleSelection:true};
B.ui.PanelMenu=B.BaseComponent.extendClass({name:"PanelMenu",init:function(F,E){D.constructor.call(this,F);
this.items={};
this.attachToDom();
this.options=C.extend(this.options,A,E||{});
this.activeItem=this.__getValueInput().value;
this.nestingLevel=0;
this.__addUserEventHandler("collapse");
this.__addUserEventHandler("expand")
},addItem:function(E){this.items[E.itemName]=E
},deleteItem:function(E){delete this.items[E.itemName]
},getSelectedItem:function(){return this.getItem(this.selectedItem())
},getItem:function(E){return this.items[E]
},selectItem:function(E){},selectedItem:function(I){if(typeof I!="undefined"){var H=this.__getValueInput();
var E=H.value;
this.activeItem=I;
H.value=I;
for(var G in this.items){var F=this.items[G];
if(F.__isSelected()){F.__unselect()
}}return E
}else{return this.activeItem
}},__getValueInput:function(){return document.getElementById(this.id+"-value")
},expandAll:function(){},collapseAll:function(){},expandGroup:function(E){},collapseGroup:function(E){},__panelMenu:function(){return C(B.getDomElement(this.id))
},__childGroups:function(){return this.__panelMenu().children(".rf-pm-top-gr")
},__addUserEventHandler:function(E){var F=this.options["on"+E];
if(F){B.Event.bindById(this.id,E,F)
}},__isActiveItem:function(E){return E.itemName==this.activeItem
},__collapseGroups:function(E){var F=E.__rfTopGroup();
this.__childGroups().each(function(G,H){if(H.id!=E.getEventElement()&&(!F||H.id!=F.id)){B.component(H).__collapse()
}})
},destroy:function(){B.Event.unbindById(this.id,"."+this.namespace);
D.destroy.call(this)
}});
var D=B.ui.PanelMenu.$super
})(RichFaces.jQuery,RichFaces);;(function(E,D){var B={charttype:"",xtype:"",ytype:"",zoom:false,grid:{clickable:true,hoverable:true},tooltip:true,tooltipOpts:{content:"%s  [%x,%y]",shifts:{x:20,y:0},defaultTheme:false},legend:{postion:"ne",sorted:"ascending"},xaxis:{min:null,max:null,autoscaleMargin:null,axisLabel:""},yaxis:{min:null,max:null,autoscaleMargin:0.2,axisLabel:""},data:[]};
var A={series:{pie:{show:true}},tooltipOpts:{content:" %p.0%, %s"}};
var C=function(H,G){var I={};
I[G+"name"]="plotclick";
I[G+"seriesIndex"]=H.data.seriesIndex;
I[G+"dataIndex"]=H.data.dataIndex;
I[G+"x"]=H.data.x;
I[G+"y"]=H.data.y;
D.ajax(G,H,{parameters:I,incId:1})
};
D.ui=D.ui||{};
D.ui.Chart=D.BaseComponent.extendClass({name:"Chart",init:function(K,J){F.constructor.call(this,K,J);
this.namespace=this.namespace||"."+RichFaces.Event.createNamespace(this.name,this.id);
this.attachToDom();
this.options=E.extend(true,{},B,J);
this.element=E(document.getElementById(K));
this.chartElement=this.element.find(".chart");
if(this.options.charttype==="pie"){this.options=E.extend(true,{},this.options,A);
this.options.data=this.options.data[0]
}else{if(this.options.charttype==="bar"){if(this.options.xtype==="string"){this.options.xaxis.tickLength=0;
var H=this.options.data[0].data.length,M=this.options.data.length,N=[],G=false;
this.options.bars=this.options.bars||{};
this.options.bars.barWidth=1/(M+1);
for(var L=0;
L<H;
L++){N.push([L,this.options.data[0].data[L][0]]);
for(var I=0;
I<M;
I++){this.options.data[I].data[L][0]=L;
if(!G){this.options.data[I].bars.order=I
}}G=true
}this.options.xaxis.ticks=N
}}else{if(J.charttype==="line"){if(this.options.xtype==="string"){this.options.xaxis.tickLength=0;
var H=this.options.data[0].data.length,M=this.options.data.length,N=[];
for(var L=0;
L<H;
L++){N.push([L,this.options.data[0].data[L][0]]);
for(var I=0;
I<M;
I++){this.options.data[I].data[L][0]=L
}}this.options.xaxis.ticks=N
}if(J.zoom){this.options.selection={mode:"xy"}
}if(this.options.xtype==="date"){this.options=E.extend({},this.options,dateDefaults);
if(this.options.xaxis.format){this.options.xaxis.timeformat=this.options.xaxis.format
}}}}}this.plot=E.plot(this.chartElement,this.options.data,this.options);
this.__bindEventHandlers(this.chartElement,this.options)
},getPlotObject:function(){return this.plot
},highlight:function(G,H){this.plot.highlight(G,H)
},unhighlight:function(G,H){this.plot.unhighlight(G,H)
},__bindEventHandlers:function(H,G){this.chartElement.on("plotclick",this._getPlotClickHandler(this.options,this.chartElement,C));
this.chartElement.on("plothover",this._getPlotHoverHandler(this.options,this.chartElement));
if(this.options.handlers&&this.options.handlers.onmouseout){this.chartElement.on("mouseout",this.options.handlers.onmouseout)
}if(this.options.zoom){this.chartElement.on("plotselected",E.proxy(this._zoomFunction,this))
}},_getPlotClickHandler:function(I,J,K){var L=I.handlers.onplotclick;
var H=I.particularSeriesHandlers.onplotclick;
var G=this.element.attr("id");
return function(O,M,N){if(N!==null){O.data={seriesIndex:N.seriesIndex,dataIndex:N.dataIndex,x:N.datapoint[0],y:N.datapoint[1],item:N};
if(I.charttype=="pie"){O.data.x=I.data[N.seriesIndex].label;
O.data.y=N.datapoint[1][0][1]
}else{if(I.charttype=="bar"&&I.xtype=="string"){O.data.x=I.xaxis.ticks[N.dataIndex][1]
}}if(I.serverSideListener){if(K){K(O,G)
}}if(L){L.call(J,O)
}if(H[O.data.seriesIndex]){H[O.data.seriesIndex].call(J,O)
}}}
},_getPlotHoverHandler:function(G,I){var H=G.handlers.onplothover;
var J=G.particularSeriesHandlers.onplothover;
return function(M,K,L){if(L!==null){M.data={seriesIndex:L.seriesIndex,dataIndex:L.dataIndex,x:L.datapoint[0],y:L.datapoint[1],item:L};
if(H){H.call(I,M)
}if(J[M.data.seriesIndex]){J[M.data.seriesIndex].call(I,M)
}}}
},_zoomFunction:function(H,G){var I=this.getPlotObject();
E.each(I.getXAxes(),function(J,K){var L=K.options;
L.min=G.xaxis.from;
L.max=G.xaxis.to
});
I.setupGrid();
I.draw();
I.clearSelection()
},resetZoom:function(){this.plot=E.plot(this.chartElement,this.options.data,this.options)
},destroy:function(){D.Event.unbindById(this.id,"."+this.namespace);
F.destroy.call(this)
}});
var F=D.ui.Chart.$super
})(RichFaces.jQuery,RichFaces);;(function(B,A){A.ui=A.ui||{};
A.ui.InputNumberSpinner=A.BaseComponent.extendClass({name:"InputNumberSpinner",cycled:true,delay:200,maxValue:100,minValue:0,step:1,init:function(H,D){C.constructor.call(this,H);
B.extend(this,D);
this.element=B(this.attachToDom());
this.input=this.element.children(".rf-insp-inp");
var F=Number(this.input.val());
if(isNaN(F)){F=this.minValue
}this.__setValue(F,null,true);
if(!this.input.attr("disabled")){var G=this.element.children(".rf-insp-btns");
this.decreaseButton=G.children(".rf-insp-dec");
this.increaseButton=G.children(".rf-insp-inc");
var E=B.proxy(this.__inputHandler,this);
this.input.change(E);
this.input.submit(E);
this.input.submit(E);
this.input.mousewheel(B.proxy(this.__mousewheelHandler,this));
this.input.keydown(B.proxy(this.__keydownHandler,this));
this.decreaseButton.mousedown(B.proxy(this.__decreaseHandler,this));
this.increaseButton.mousedown(B.proxy(this.__increaseHandler,this))
}},decrease:function(D){var E=this.value-this.step;
E=this.roundFloat(E);
if(E<this.minValue&&this.cycled){E=this.maxValue
}this.__setValue(E,D)
},increase:function(D){var E=this.value+this.step;
E=this.roundFloat(E);
if(E>this.maxValue&&this.cycled){E=this.minValue
}this.__setValue(E,D)
},getValue:function(){return this.value
},setValue:function(E,D){if(!this.input.attr("disabled")){this.__setValue(E)
}},roundFloat:function(D){var G=this.step.toString();
var F=0;
if(!/\./.test(G)){if(this.step>=1){return D
}if(/e/.test(G)){F=G.split("-")[1]
}}else{F=G.length-G.indexOf(".")-1
}var E=D.toFixed(F);
return parseFloat(E)
},destroy:function(D){if(this.intervalId){window.clearInterval(this.intervalId);
this.decreaseButton.css("backgroundPosition"," 50% 40%").unbind("mouseout",this.destroy).unbind("mouseup",this.destroy);
this.increaseButton.css("backgroundPosition"," 50% 40%").unbind("mouseout",this.destroy).unbind("mouseup",this.destroy);
this.intervalId=null
}C.destroy.call(this)
},__setValue:function(E,D,F){if(!isNaN(E)){if(E>this.maxValue){E=this.maxValue;
this.input.val(E)
}else{if(E<this.minValue){E=this.minValue;
this.input.val(E)
}}if(E!=this.value){this.input.val(E);
this.value=E;
if(this.onchange&&!F){this.onchange.call(this.element[0],D)
}}}},__inputHandler:function(D){var E=Number(this.input.val());
if(isNaN(E)){this.input.val(this.value)
}else{this.__setValue(E,D)
}},__mousewheelHandler:function(F,G,E,D){G=E||D;
if(G>0){this.increase(F)
}else{if(G<0){this.decrease(F)
}}return false
},__keydownHandler:function(D){if(D.keyCode==40){this.decrease(D);
D.preventDefault()
}else{if(D.keyCode==38){this.increase(D);
D.preventDefault()
}}},__decreaseHandler:function(F){var D=this;
D.decrease(F);
this.intervalId=window.setInterval(function(){D.decrease(F)
},this.delay);
var E=B.proxy(this.destroy,this);
this.decreaseButton.bind("mouseup",E).bind("mouseout",E).css("backgroundPosition","60% 60%");
F.preventDefault()
},__increaseHandler:function(F){var D=this;
D.increase(F);
this.intervalId=window.setInterval(function(){D.increase(F)
},this.delay);
var E=B.proxy(this.destroy,this);
this.increaseButton.bind("mouseup",E).bind("mouseout",E).css("backgroundPosition","60% 60%");
F.preventDefault()
}});
var C=A.ui.InputNumberSpinner.$super
}(RichFaces.jQuery,window.RichFaces));;(function(F,D){D.ui=D.ui||{};
D.ui.NotifyMessage=function(K,J,I){H.constructor.call(this,K,J,A);
this.notifyOptions=I
};
D.ui.Base.extend(D.ui.NotifyMessage);
var H=D.ui.NotifyMessage.$super;
var A={showSummary:true,level:0,isMessages:false,globalOnly:false};
var G=function(K,I,M){var L=M.sourceId;
var J=M.message;
if(!this.options.forComponentId){if(!this.options.globalOnly&&J){E.call(this,L,J)
}}else{if(this.options.forComponentId===L){E.call(this,L,J)
}}};
var E=function(I,J){if(J&&J.severity>=this.options.level){C.call(this,J)
}};
var C=function(I){D.ui.Notify(F.extend({},this.notifyOptions,{summary:this.options.showSummary?I.summary:undefined,detail:this.options.showDetail?I.detail:undefined,severity:I.severity}))
};
var B=function(){D.Event.bind(window.document,D.Event.MESSAGE_EVENT_TYPE+this.namespace,G,this)
};
F.extend(D.ui.NotifyMessage.prototype,{name:"NotifyMessage",__bindEventHandlers:B,destroy:function(){D.Event.unbind(window.document,D.Event.MESSAGE_EVENT_TYPE+this.namespace);
H.destroy.call(this)
}})
})(RichFaces.jQuery,window.RichFaces||(window.RichFaces={}));;(function(D,I){I.ui=I.ui||{};
I.ui.FileUpload=function(O,M){this.id=O;
this.items=[];
this.submitedItems=[];
D.extend(this,M);
if(this.acceptedTypes){this.acceptedTypes=D.trim(this.acceptedTypes).toUpperCase().split(/\s*,\s*/)
}if(this.maxFilesQuantity){this.maxFilesQuantity=parseInt(D.trim(this.maxFilesQuantity))
}this.element=D(this.attachToDom());
this.form=this.element.parents("form:first");
var N=this.element.children(".rf-fu-hdr:first");
var L=N.children(".rf-fu-btns-lft:first");
this.addButton=L.children(".rf-fu-btn-add:first");
this.uploadButton=this.addButton.next();
this.clearButton=L.next().children(".rf-fu-btn-clr:first");
this.inputContainer=this.addButton.find(".rf-fu-inp-cntr:first");
this.input=this.inputContainer.children("input");
this.list=N.next();
this.element.bind("dragenter",function(P){P.stopPropagation();
P.preventDefault()
});
this.element.bind("dragover",function(P){P.stopPropagation();
P.preventDefault()
});
this.element.bind("drop",D.proxy(this.__addItemsFromDrop,this));
this.hiddenContainer=this.list.next();
this.cleanInput=this.input.clone();
this.addProxy=D.proxy(this.__addItems,this);
this.input.change(this.addProxy);
this.addButton.mousedown(E).mouseup(H).mouseout(H);
this.uploadButton.click(D.proxy(this.__startUpload,this)).mousedown(E).mouseup(H).mouseout(H);
this.clearButton.click(D.proxy(this.__removeAllItems,this)).mousedown(E).mouseup(H).mouseout(H);
if(this.onfilesubmit){I.Event.bind(this.element,"onfilesubmit",new Function("event",this.onfilesubmit))
}if(this.ontyperejected){I.Event.bind(this.element,"ontyperejected",new Function("event",this.ontyperejected))
}if(this.onuploadcomplete){I.Event.bind(this.element,"onuploadcomplete",new Function("event",this.onuploadcomplete))
}if(this.onclear){I.Event.bind(this.element,"onclear",new Function("event",this.onclear))
}if(this.onfileselect){I.Event.bind(this.element,"onfileselect",new Function("event",this.onfileselect))
}};
var A="rf_fu_uid";
var J="rf_fu_uid_alt";
var K="C:\\fakepath\\";
var G='<div class="rf-fu-itm"><span class="rf-fu-itm-lft"><span class="rf-fu-itm-lbl"/><span class="rf-fu-itm-st" /><div class="progress progress-striped active"><div class="progress-bar" role="progressbar" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100" style="width: 0%"><span></span></div></div></span><span class="rf-fu-itm-rgh"><a href="javascript:void(0)" class="rf-fu-itm-lnk"/></span></div>';
var F={NEW:"new",UPLOADING:"uploading",DONE:"done",SIZE_EXCEEDED:"sizeExceeded",STOPPED:"stopped",SERVER_ERROR_PROCESS:"serverErrorProc",SERVER_ERROR_UPLOAD:"serverErrorUp"};
var E=function(L){D(this).children(":first").css("background-position","3px 3px").css("padding","4px 4px 2px 22px")
};
var H=function(L){D(this).children(":first").css("background-position","2px 2px").css("padding","3px 5px 3px 21px")
};
I.BaseComponent.extend(I.ui.FileUpload);
function B(L){this.name="TypeRejectedException";
this.message="The type of file "+L+" is not accepted";
this.fileName=L
}D.extend(I.ui.FileUpload.prototype,(function(){return{name:"FileUpload",doneLabel:"Done",sizeExceededLabel:"File size is exceeded",stoppedLabel:"",serverErrorProcLabel:"Server error: error in processing",serverErrorUpLabel:"Server error: upload failed",clearLabel:"Clear",deleteLabel:"Delete",__addFiles:function(N){var M={acceptedFileNames:[],rejectedFileNames:[]};
if(N){for(var L=0;
L<N.length;
L++){this.__tryAddItem(M,N[L]);
if(this.maxFilesQuantity&&this.__getTotalItemCount()>=this.maxFilesQuantity){this.addButton.hide();
break
}}}else{var O=this.input.val();
this.__tryAddItem(M,O)
}if(M.rejectedFileNames.length>0){I.Event.fire(this.element,"ontyperejected",M.rejectedFileNames.join(","))
}if(this.immediateUpload){this.__startUpload()
}},__addItems:function(){this.__addFiles(this.input.prop("files"))
},__addItemsFromDrop:function(L){L.stopPropagation();
L.preventDefault();
if(this.maxFilesQuantity&&this.__getTotalItemCount()>=this.maxFilesQuantity){return 
}this.__addFiles(L.originalEvent.dataTransfer.files)
},__tryAddItem:function(M,L){try{if(this.__addItem(L)){M.acceptedFileNames.push(L.name)
}}catch(N){if(N instanceof B){M.rejectedFileNames.push(L.name)
}else{throw N
}}},__addItem:function(L){var N=L.name;
if(!navigator.platform.indexOf("Win")){N=N.match(/[^\\]*$/)[0]
}else{if(!N.indexOf(K)){N=N.substr(K.length)
}else{N=N.match(/[^\/]*$/)[0]
}}if(this.__accept(N)&&(!this.noDuplicate||!this.__isFileAlreadyAdded(N))){this.input.remove();
this.input.unbind("change",this.addProxy);
var M=new C(this,L);
this.list.append(M.getJQuery());
this.items.push(M);
this.input=this.cleanInput.clone();
this.inputContainer.append(this.input);
this.input.change(this.addProxy);
this.__updateButtons();
I.Event.fire(this.element,"onfileselect",N);
return true
}return false
},__removeItem:function(L){this.items.splice(D.inArray(L,this.items),1);
this.submitedItems.splice(D.inArray(L,this.submitedItems),1);
this.__updateButtons();
I.Event.fire(this.element,"onclear",[L.model])
},__removeAllItems:function(M){var N=[];
for(var L in this.submitedItems){N.push(this.submitedItems[L].model)
}for(var L in this.items){N.push(this.items[L].model)
}this.list.empty();
this.items.splice(0,this.items.length);
this.submitedItems.splice(0,this.submitedItems.length);
this.__updateButtons();
I.Event.fire(this.element,"onclear",N)
},__updateButtons:function(){if(!this.loadableItem&&this.list.children(".rf-fu-itm").size()){if(this.items.length){this.uploadButton.css("display","inline-block")
}else{this.uploadButton.hide()
}this.clearButton.css("display","inline-block")
}else{this.uploadButton.hide();
this.clearButton.hide()
}if(this.maxFilesQuantity&&this.__getTotalItemCount()>=this.maxFilesQuantity){this.addButton.hide()
}else{this.addButton.css("display","inline-block")
}},__startUpload:function(){if(!this.items.length){this.__finishUpload();
return 
}this.loadableItem=this.items.shift();
this.__updateButtons();
this.loadableItem.startUploading()
},__accept:function(O){O=O.toUpperCase();
var L=!this.acceptedTypes;
for(var M=0;
!L&&M<this.acceptedTypes.length;
M++){var N="."+this.acceptedTypes[M];
if(N==="."&&O.indexOf(".")<0){L=true
}else{L=O.indexOf(N,O.length-N.length)!==-1
}}if(!L){throw new B(O)
}return L
},__isFileAlreadyAdded:function(N){var L=false;
for(var M=0;
!L&&M<this.items.length;
M++){L=this.items[M].model.name==N
}L=L||(this.loadableItem&&this.loadableItem.model.name==N);
for(var M=0;
!L&&M<this.submitedItems.length;
M++){L=this.submitedItems[M].model.name==N
}return L
},__getTotalItemCount:function(){return this.__getItemCountByState(this.items,F.NEW)+this.__getItemCountByState(this.submitedItems,F.DONE)
},__getItemCountByState:function(L){var O={};
var N=0;
for(var M=1;
M<arguments.length;
M++){O[arguments[M]]=true
}for(var M=0;
M<L.length;
M++){if(O[L[M].model.state]){N++
}}return N
},__finishUpload:function(){this.loadableItem=null;
this.__updateButtons();
var L=[];
for(var M in this.submitedItems){L.push(this.submitedItems[M].model)
}for(var M in this.items){L.push(this.items[M].model)
}I.Event.fire(this.element,"onuploadcomplete",L)
}}
})());
var C=function(M,L){this.fileUpload=M;
this.model={name:L.name,state:F.NEW,file:L}
};
D.extend(C.prototype,{getJQuery:function(){this.element=D(G);
var L=this.element.children(".rf-fu-itm-lft:first");
this.label=L.children(".rf-fu-itm-lbl:first");
this.state=this.label.nextAll(".rf-fu-itm-st:first");
this.progressBar=L.find(".progress-bar");
this.progressBar.parent().hide();
this.progressLabel=this.progressBar.find("span");
this.link=L.next().children("a");
this.label.html(this.model.name);
this.link.html(this.fileUpload.deleteLabel);
this.link.click(D.proxy(this.removeOrStop,this));
return this.element
},removeOrStop:function(){this.element.remove();
this.fileUpload.__removeItem(this)
},startUploading:function(){this.state.css("display","block");
this.progressBar.parent().show();
this.progressLabel.html("0 %");
this.link.html("");
this.model.state=F.UPLOADING;
this.uid=Math.random();
var O=new FormData(this.fileUpload.form[0]);
fileName=this.model.file.name;
O.append(this.fileUpload.id,this.model.file);
var M=this.fileUpload.form.attr("action"),L=M.indexOf("?")==-1?"?":"&",N=M+L+A+"="+this.uid+"&javax.faces.partial.ajax=true&javax.faces.source="+this.fileUpload.id+"&javax.faces.partial.execute="+this.fileUpload.id+"&org.richfaces.ajax.component="+this.fileUpload.id+"&"+jsf.getViewState(this.fileUpload.form[0]);
if(jsf.getClientWindow&&jsf.getClientWindow()){N+="&javax.faces.ClientWindow="+jsf.getClientWindow()
}this.xhr=new XMLHttpRequest();
this.xhr.open("POST",N,true);
this.xhr.setRequestHeader("Faces-Request","partial/ajax");
this.xhr.upload.onprogress=D.proxy(function(Q){if(Q.lengthComputable){var P=Math.floor((Q.loaded/Q.total)*100);
this.progressLabel.html(P+" %");
this.progressBar.attr("aria-valuenow",P);
this.progressBar.css("width",P+"%")
}},this);
this.xhr.upload.onerror=D.proxy(function(P){this.fileUpload.loadableItem=null;
this.finishUploading(F.SERVER_ERROR_UPLOAD)
},this);
this.xhr.onload=D.proxy(function(P){switch(P.target.status){case 413:responseStatus=F.SIZE_EXCEEDED;
break;
case 200:responseStatus=F.DONE;
break;
default:responseStatus=F.SERVER_ERROR_PROCESS
}var Q={source:this.fileUpload.element[0],element:this.fileUpload.element[0],_mfInternal:{_mfSourceControlId:this.fileUpload.element.attr("id")}};
jsf.ajax.response(this.xhr,Q);
this.finishUploading(responseStatus);
this.fileUpload.__startUpload()
},this);
this.xhr.send(O);
I.Event.fire(this.fileUpload.element,"onfilesubmit",this.model)
},finishUploading:function(L){this.state.html(this.fileUpload[L+"Label"]);
this.progressBar.parent().hide();
this.link.html(this.fileUpload.clearLabel);
this.model.state=L
}})
}(RichFaces.jQuery,window.RichFaces));;(function(D,A){A.utils=A.utils||{};
A.utils.addCSSText=function(H,F){var G=D("<style></style>").attr({type:"text/css",id:F}).appendTo("head");
try{G.html(H)
}catch(I){G[0].styleSheet.cssText=H
}};
A.utils.Ranges=function(){this.ranges=[]
};
A.utils.Ranges.prototype={add:function(F){var G=0;
while(G<this.ranges.length&&F>=this.ranges[G++][1]){}G--;
if(this.ranges[G-1]&&F==(this.ranges[G-1][1]+1)){if(F==(this.ranges[G][0]-1)){this.ranges[G-1][1]=this.ranges[G][1];
this.ranges.splice(G,1)
}else{this.ranges[G-1][1]++
}}else{if(this.ranges[G]){if(this.ranges[G]&&F==(this.ranges[G][0]-1)){this.ranges[G][0]--
}else{if(F==(this.ranges[G][1]+1)){this.ranges[G][1]++
}else{if(F<this.ranges[G][1]){this.ranges.splice(G,0,[F,F])
}else{this.ranges.splice(G+1,0,[F,F])
}}}}else{this.ranges.splice(G,0,[F,F])
}}},remove:function(F){var G=0;
while(G<this.ranges.length&&F>this.ranges[G++][1]){}G--;
if(this.ranges[G]){if(F==(this.ranges[G][1])){if(F==(this.ranges[G][0])){this.ranges.splice(G,1)
}else{this.ranges[G][1]--
}}else{if(F==(this.ranges[G][0])){this.ranges[G][0]++
}else{this.ranges.splice(G+1,0,[F+1,this.ranges[G][1]]);
this.ranges[G][1]=F-1
}}}},clear:function(){this.ranges=[]
},contains:function(F){var G=0;
while(G<this.ranges.length&&F>=this.ranges[G][0]){if(F>=this.ranges[G][0]&&F<=this.ranges[G][1]){return true
}else{G++
}}return false
},toString:function(){var F=new Array(this.ranges.length);
for(var G=0;
G<this.ranges.length;
G++){F[G]=this.ranges[G].join()
}return F.join(";")
}};
var B="rf-edt-c-";
var C=20;
A.ui=A.ui||{};
A.ui.ExtendedDataTable=A.BaseComponent.extendClass({name:"ExtendedDataTable",init:function(K,H,G,I){E.constructor.call(this,K);
this.ranges=new A.utils.Ranges();
this.rowCount=H;
this.ajaxFunction=G;
this.options=I||{};
this.element=this.attachToDom();
this.newWidths={};
this.storeDomReferences();
if(this.options.onready&&typeof this.options.onready=="function"){A.Event.bind(this.element,"rich:ready",this.options.onready)
}this.resizeEventName="resize.rf.edt."+this.id;
D(document).ready(D.proxy(this.initialize,this));
this.activateResizeListener();
var F=D(this.element).find(".rf-edt-b .rf-edt-cnt");
var J=function(M,L){return function(){setTimeout(function(){M.scrollElement.scrollLeft=L.scrollLeft();
M.updateScrollPosition()
},0)
}
};
F.bind("scroll",J(this,F));
D(this.scrollElement).bind("scroll",D.proxy(this.updateScrollPosition,this));
this.bindHeaderHandlers();
D(this.element).bind("rich:onajaxcomplete",D.proxy(this.ajaxComplete,this));
this.resizeData={};
this.idOfReorderingColumn="";
this.timeoutId=null
},storeDomReferences:function(){this.dragElement=document.getElementById(this.id+":d");
this.reorderElement=document.getElementById(this.id+":r");
this.reorderMarkerElement=document.getElementById(this.id+":rm");
this.widthInput=document.getElementById(this.id+":wi");
this.selectionInput=document.getElementById(this.id+":si");
this.header=D(this.element).children(".rf-edt-hdr");
this.headerCells=this.header.find(".rf-edt-hdr-c");
this.footerCells=D(this.element).children(".rf-edt-ftr").find(".rf-edt-ftr-c");
this.resizerHolders=this.header.find(".rf-edt-rsz-cntr");
this.frozenHeaderPartElement=document.getElementById(this.id+":frozenHeader");
this.frozenColumnCount=this.frozenHeaderPartElement?this.frozenHeaderPartElement.children[0].rows[0].cells.length:0;
this.headerElement=document.getElementById(this.id+":header");
this.footerElement=document.getElementById(this.id+":footer");
this.scrollElement=document.getElementById(this.id+":scrl");
this.scrollContentElement=document.getElementById(this.id+":scrl-cnt")
},getColumnPosition:function(H){var F;
for(var G=0;
G<this.headerCells.length;
G++){if(H==this.headerCells[G].className.match(new RegExp(B+"([^\\W]*)"))[1]){F=G
}}return F
},setColumnPosition:function(K,F){var J="";
var H;
for(var G=0;
G<this.headerCells.length;
G++){var I=this.headerCells[G].className.match(new RegExp(B+"([^\\W]*)"))[1];
if(G==F){if(H){J+=I+","+K+","
}else{J+=K+","+I+","
}}else{if(K!=I){J+=I+","
}else{H=true
}}}this.ajaxFunction(null,{"rich:columnsOrder":J})
},setColumnWidth:function(I,G){G=G+"px";
var F=D(document.getElementById(this.element.id));
F.find("."+B+I).parent().css("width",G);
F.find("."+B+I).css("width",G);
this.newWidths[I]=G;
var H=new Array();
for(var J in this.newWidths){H.push(J+":"+this.newWidths[J])
}this.widthInput.value=H.toString();
this.updateLayout();
this.adjustResizers();
this.ajaxFunction()
},filter:function(H,I,F){if(typeof (I)=="undefined"||I==null){I=""
}var G={};
G[this.id+"rich:filtering"]=H+":"+I+":"+F;
this.ajaxFunction(null,G)
},clearFiltering:function(){this.filter("","",true)
},sortHandler:function(I){var F=D(I.data.sortHandle);
var G=F.find(".rf-edt-srt-btn");
var J=G.data("columnid");
var H=G.hasClass("rf-edt-srt-asc")?"descending":"ascending";
this.sort(J,H,false)
},filterHandler:function(G){var F=D(G.data.filterHandle);
var H=F.data("columnid");
var I=F.val();
this.filter(H,I,false)
},sort:function(I,G,F){if(typeof (G)=="string"){G=G.toLowerCase()
}var H={};
H[this.id+"rich:sorting"]=I+":"+G+":"+F;
this.ajaxFunction(null,H)
},clearSorting:function(){this.sort("","",true)
},destroy:function(){D(window).unbind("resize",this.updateLayout);
D(A.getDomElement(this.id+":st")).remove();
E.destroy.call(this)
},bindHeaderHandlers:function(){this.header.find(".rf-edt-rsz").bind("mousedown",D.proxy(this.beginResize,this));
this.headerCells.bind("mousedown",D.proxy(this.beginReorder,this));
var F=this;
this.header.find(".rf-edt-c-srt").each(function(){D(this).bind("click",{sortHandle:this},D.proxy(F.sortHandler,F))
});
this.header.find(".rf-edt-flt-i").each(function(){D(this).bind("blur",{filterHandle:this},D.proxy(F.filterHandler,F))
})
},updateLayout:function(){this.deActivateResizeListener();
this.headerCells.height("auto");
var L=0;
this.headerCells.each(function(){if(this.clientHeight>L){L=this.clientHeight
}});
this.headerCells.height(L+"px");
this.footerCells.height("auto");
var H=0;
this.footerCells.each(function(){if(this.clientHeight>H){H=this.clientHeight
}});
this.footerCells.height(H+"px");
this.contentDivElement.css("width","auto");
var K=this.frozenHeaderPartElement?this.frozenHeaderPartElement.offsetWidth:0;
var J=Math.max(0,this.element.clientWidth-K);
if(J){this.parts.each(function(){this.style.width="auto"
});
var G=this.parts.width();
if(G>J){this.contentDivElement.css("width",J+"px")
}this.contentDivElement.css("display","block");
if(G>J){this.parts.each(function(){this.style.width=J+"px"
});
this.scrollElement.style.display="block";
this.scrollElement.style.overflowX="scroll";
this.scrollElement.style.width=J+"px";
this.scrollContentElement.style.width=G+"px";
this.updateScrollPosition()
}else{this.parts.each(function(){this.style.width=""
});
this.scrollElement.style.display="none"
}}else{this.contentDivElement.css("display","none")
}var F=this.element.clientHeight;
var I=this.element.firstChild;
while(I&&(!I.nodeName||I.nodeName.toUpperCase()!="TABLE")){if(I.nodeName&&I.nodeName.toUpperCase()=="DIV"&&I!=this.bodyElement){F-=I.offsetHeight
}I=I.nextSibling
}if(this.bodyElement.offsetHeight>F||!this.contentElement){this.bodyElement.style.height=F+"px"
}this.activateResizeListener()
},adjustResizers:function(){var H=this.scrollElement?this.scrollElement.scrollLeft:0;
var G=this.element.clientWidth-3;
var F=0;
for(;
F<this.frozenColumnCount;
F++){if(G>0){this.resizerHolders[F].style.display="none";
this.resizerHolders[F].style.display="";
G-=this.resizerHolders[F].offsetWidth
}if(G<=0){this.resizerHolders[F].style.display="none"
}}H-=3;
for(;
F<this.resizerHolders.length;
F++){if(G>0){this.resizerHolders[F].style.display="none";
if(H>0){this.resizerHolders[F].style.display="";
H-=this.resizerHolders[F].offsetWidth;
if(H>0){this.resizerHolders[F].style.display="none"
}else{G+=H
}}else{this.resizerHolders[F].style.display="";
G-=this.resizerHolders[F].offsetWidth
}}if(G<=0){this.resizerHolders[F].style.display="none"
}}},updateScrollPosition:function(){if(this.scrollElement){var F=this.scrollElement.scrollLeft;
this.parts.each(function(){this.scrollLeft=F
})
}this.adjustResizers()
},initialize:function(){this.deActivateResizeListener();
if(!D(this.element).is(":visible")){this.showOffscreen(this.element)
}this.bodyElement=document.getElementById(this.id+":b");
this.bodyElement.tabIndex=-1;
this.contentDivElement=D(this.bodyElement).find(".rf-edt-cnt");
var F=D(this.bodyElement);
this.contentElement=F.children("div:not(.rf-edt-ndt):first")[0];
if(this.contentElement){this.spacerElement=this.contentElement.children[0];
this.dataTableElement=this.contentElement.lastChild;
this.tbodies=D(document.getElementById(this.id+":tbf")).add(document.getElementById(this.id+":tbn"));
this.rows=this.tbodies[0].rows.length;
this.rowHeight=this.dataTableElement.offsetHeight/this.rows;
if(this.rowCount!=this.rows){this.contentElement.style.height=(this.rowCount*this.rowHeight)+"px"
}F.bind("scroll",D.proxy(this.bodyScrollListener,this));
if(this.options.selectionMode!="none"){this.tbodies.bind("click",D.proxy(this.selectionClickListener,this));
F.bind(window.opera?"keypress":"keydown",D.proxy(this.selectionKeyDownListener,this));
this.initializeSelection()
}}else{this.spacerElement=null;
this.dataTableElement=null
}var G=this.element;
this.parts=D(this.element).find(".rf-edt-cnt, .rf-edt-ftr-cnt").filter(function(){return D(this).parents(".rf-edt").get(0)===G
});
this.updateLayout();
this.updateScrollPosition();
if(D(this.element).data("offscreenElements")){this.hideOffscreen(this.element)
}this.activateResizeListener();
D(this.element).trigger("rich:ready",this)
},showOffscreen:function(G){var F=D(G);
var I=F.parents(":not(:visible)").addBack().toArray().reverse();
var H=this;
D.each(I,function(){$this=D(this);
if($this.css("display")==="none"){H.showOffscreenElement(D(this))
}});
F.data("offscreenElements",I)
},hideOffscreen:function(G){var F=D(G);
var I=F.data("offscreenElements");
var H=this;
D.each(I,function(){$this=D(this);
if($this.data("offscreenOldValues")){H.hideOffscreenElement(D(this))
}});
F.removeData("offscreenElements")
},showOffscreenElement:function(F){var G={};
G.oldPosition=F.css("position");
G.oldLeft=F.css("left");
G.oldDisplay=F.css("display");
F.css("position","absolute");
F.css("left","-10000");
F.css("display","block");
F.data("offscreenOldValues",G)
},hideOffscreenElement:function(F){var G=F.data("offscreenOldValues");
F.css("display",G.oldDisplay);
F.css("left",G.oldLeft);
F.css("position",G.oldPosition);
F.removeData("offscreenOldValues")
},drag:function(F){D(this.dragElement).setPosition({left:Math.max(this.resizeData.left+C,F.pageX)});
return false
},beginResize:function(F){var G=F.currentTarget.parentNode.className.match(new RegExp(B+"([^\\W]*)"))[1];
this.resizeData={id:G,left:D(F.currentTarget).parent().offset().left};
this.dragElement.style.height=this.element.offsetHeight+"px";
D(this.dragElement).setPosition({top:D(this.element).offset().top,left:F.pageX});
this.dragElement.style.display="block";
D(document).bind("mousemove",D.proxy(this.drag,this));
D(document).one("mouseup",D.proxy(this.endResize,this));
return false
},endResize:function(G){D(document).unbind("mousemove",this.drag);
this.dragElement.style.display="none";
var F=Math.max(C,G.pageX-this.resizeData.left);
this.setColumnWidth(this.resizeData.id,F)
},reorder:function(F){D(this.reorderElement).setPosition(F,{offset:[5,5]});
this.reorderElement.style.display="block";
return false
},beginReorder:function(F){if(!D(F.target).is("a, img, :input")){this.idOfReorderingColumn=F.currentTarget.className.match(new RegExp(B+"([^\\W]*)"))[1];
D(document).bind("mousemove",D.proxy(this.reorder,this));
this.headerCells.bind("mouseover",D.proxy(this.overReorder,this));
D(document).one("mouseup",D.proxy(this.cancelReorder,this));
return false
}},overReorder:function(G){if(this.idOfReorderingColumn!=G.currentTarget.className.match(new RegExp(B+"([^\\W]*)"))[1]){var F=D(G.currentTarget);
var H=F.offset();
D(this.reorderMarkerElement).setPosition({top:H.top+F.height(),left:H.left-5});
this.reorderMarkerElement.style.display="block";
F.one("mouseout",D.proxy(this.outReorder,this));
F.one("mouseup",D.proxy(this.endReorder,this))
}},outReorder:function(F){this.reorderMarkerElement.style.display="";
D(F.currentTarget).unbind("mouseup",this.endReorder)
},endReorder:function(F){this.reorderMarkerElement.style.display="";
D(F.currentTarget).unbind("mouseout",this.outReorder);
var I=F.currentTarget.className.match(new RegExp(B+"([^\\W]*)"))[1];
var H="";
var G=this;
this.headerCells.each(function(){var J=this.className.match(new RegExp(B+"([^\\W]*)"))[1];
if(J==I){H+=G.idOfReorderingColumn+","+I+","
}else{if(J!=G.idOfReorderingColumn){H+=J+","
}}});
this.ajaxFunction(F,{"rich:columnsOrder":H})
},cancelReorder:function(F){D(document).unbind("mousemove",this.reorder);
this.headerCells.unbind("mouseover",this.overReorder);
this.reorderElement.style.display="none"
},loadData:function(G){var F=Math.round((this.bodyElement.scrollTop+this.bodyElement.clientHeight/2)/this.rowHeight-this.rows/2);
if(F<=0){F=0
}else{F=Math.min(this.rowCount-this.rows,F)
}this.ajaxFunction(G,{"rich:clientFirst":F})
},bodyScrollListener:function(F){if(this.timeoutId){window.clearTimeout(this.timeoutId);
this.timeoutId=null
}if(Math.max(F.currentTarget.scrollTop-this.rowHeight,0)<this.spacerElement.offsetHeight||Math.min(F.currentTarget.scrollTop+this.rowHeight+F.currentTarget.clientHeight,F.currentTarget.scrollHeight)>this.spacerElement.offsetHeight+this.dataTableElement.offsetHeight){var G=this;
this.timeoutId=window.setTimeout(function(H){G.loadData(H)
},1000)
}},showActiveRow:function(){if(this.bodyElement.scrollTop>this.activeIndex*this.rowHeight+this.spacerElement.offsetHeight){this.bodyElement.scrollTop=Math.max(this.bodyElement.scrollTop-this.rowHeight,0)
}else{if(this.bodyElement.scrollTop+this.bodyElement.clientHeight<(this.activeIndex+1)*this.rowHeight+this.spacerElement.offsetHeight){this.bodyElement.scrollTop=Math.min(this.bodyElement.scrollTop+this.rowHeight,this.bodyElement.scrollHeight-this.bodyElement.clientHeight)
}}},selectRow:function(F){this.ranges.add(F);
for(var G=0;
G<this.tbodies.length;
G++){D(this.tbodies[G].rows[F]).addClass("rf-edt-r-sel")
}},deselectRow:function(F){this.ranges.remove(F);
for(var G=0;
G<this.tbodies.length;
G++){D(this.tbodies[G].rows[F]).removeClass("rf-edt-r-sel")
}},setActiveRow:function(F){if(typeof this.activeIndex=="number"){for(var G=0;
G<this.tbodies.length;
G++){D(this.tbodies[G].rows[this.activeIndex]).removeClass("rf-edt-r-act")
}}this.activeIndex=F;
for(var G=0;
G<this.tbodies.length;
G++){D(this.tbodies[G].rows[this.activeIndex]).addClass("rf-edt-r-act")
}},resetShiftRow:function(){if(typeof this.shiftIndex=="number"){for(var F=0;
F<this.tbodies.length;
F++){D(this.tbodies[F].rows[this.shiftIndex]).removeClass("rf-edt-r-sht")
}}this.shiftIndex=null
},setShiftRow:function(F){this.resetShiftRow();
this.shiftIndex=F;
if(typeof F=="number"){for(var G=0;
G<this.tbodies.length;
G++){D(this.tbodies[G].rows[this.shiftIndex]).addClass("rf-edt-r-sht")
}}},initializeSelection:function(){this.ranges.clear();
var F=this.selectionInput.value.split("|");
this.activeIndex=F[1]||null;
this.shiftIndex=F[2]||null;
this.selectionFlag=null;
var H=this.tbodies[0].rows;
for(var G=0;
G<H.length;
G++){var I=D(H[G]);
if(I.hasClass("rf-edt-r-sel")){this.ranges.add(I[0].rowIndex)
}if(I.hasClass("rf-edt-r-act")){this.activeIndex=I[0].rowIndex
}if(I.hasClass("rf-edt-r-sht")){this.shiftIndex=I[0].rowIndex
}}this.writeSelection()
},writeSelection:function(){this.selectionInput.value=[this.ranges,this.activeIndex,this.shiftIndex,this.selectionFlag].join("|")
},selectRows:function(F){if(typeof F=="number"){F=[F,F]
}var H;
var G=0;
for(;
G<F[0];
G++){if(this.ranges.contains(G)){this.deselectRow(G);
H=true
}}for(;
G<=F[1];
G++){if(!this.ranges.contains(G)){this.selectRow(G);
H=true
}}for(;
G<this.rows;
G++){if(this.ranges.contains(G)){this.deselectRow(G);
H=true
}}this.selectionFlag=typeof this.shiftIndex=="string"?this.shiftIndex:"x";
return H
},processSlectionWithShiftKey:function(G){if(this.shiftIndex==null){this.setShiftRow(this.activeIndex!=null?this.activeIndex:G)
}var F;
if("u"==this.shiftIndex){F=[0,G]
}else{if("d"==this.shiftIndex){F=[G,this.rows-1]
}else{if(G>=this.shiftIndex){F=[this.shiftIndex,G]
}else{F=[G,this.shiftIndex]
}}}return this.selectRows(F)
},onbeforeselectionchange:function(F){return !this.options.onbeforeselectionchange||this.options.onbeforeselectionchange.call(this.element,F)!=false
},onselectionchange:function(G,F,H){if(!G.shiftKey){this.resetShiftRow()
}if(this.activeIndex!=F){this.setActiveRow(F);
this.showActiveRow()
}if(H){this.writeSelection();
if(this.options.onselectionchange){this.options.onselectionchange.call(this.element,G)
}}},selectionClickListener:function(G){if(!this.onbeforeselectionchange(G)){return 
}var I;
if(G.shiftKey||G.ctrlKey){if(window.getSelection){window.getSelection().removeAllRanges()
}else{if(document.selection){document.selection.empty()
}}}var H=G.target;
while(this.tbodies.index(H.parentNode)==-1){H=H.parentNode
}var F=H.rowIndex;
if(typeof (F)==="undefined"){return 
}if(this.options.selectionMode=="single"||(this.options.selectionMode!="multipleKeyboardFree"&&!G.shiftKey&&!G.ctrlKey)){I=this.selectRows(F)
}else{if(this.options.selectionMode=="multipleKeyboardFree"||(!G.shiftKey&&G.ctrlKey)){if(this.ranges.contains(F)){this.deselectRow(F)
}else{this.selectRow(F)
}I=true
}else{I=this.processSlectionWithShiftKey(F)
}}this.onselectionchange(G,F,I)
},selectionKeyDownListener:function(G){if(G.ctrlKey&&this.options.selectionMode!="single"&&(G.keyCode==65||G.keyCode==97)&&this.onbeforeselectionchange(G)){this.selectRows([0,this.rows]);
this.selectionFlag="a";
this.onselectionchange(G,this.activeIndex,true);
G.preventDefault()
}else{var F;
if(G.keyCode==38){F=-1
}else{if(G.keyCode==40){F=1
}}if(F!=null&&this.onbeforeselectionchange(G)){if(typeof this.activeIndex=="number"){F+=this.activeIndex;
if(F>=0&&F<this.rows){var H;
if(this.options.selectionMode=="single"||(!G.shiftKey&&!G.ctrlKey)){H=this.selectRows(F)
}else{if(G.shiftKey){H=this.processSlectionWithShiftKey(F)
}}this.onselectionchange(G,F,H)
}}G.preventDefault()
}}},ajaxComplete:function(H,I){this.storeDomReferences();
if(I.reinitializeHeader){this.bindHeaderHandlers();
this.updateLayout()
}else{this.selectionInput=document.getElementById(this.id+":si");
if(I.reinitializeBody){this.rowCount=I.rowCount;
this.initialize()
}else{if(this.options.selectionMode!="none"){this.initializeSelection()
}}if(this.spacerElement){this.spacerElement.style.height=(I.first*this.rowHeight)+"px"
}}var F=D(document.getElementById(this.element.id)),G=new Array();
for(var J in this.newWidths){F.find("."+B+J).css("width",this.newWidths[J]).parent().css("width",this.newWidths[J]);
G.push(J+":"+this.newWidths[J])
}this.widthInput.value=G.toString();
this.updateLayout();
this.adjustResizers()
},activateResizeListener:function(){if(typeof this.resizeEventName!=="undefined"){D(window).on(this.resizeEventName,D.proxy(this.updateLayout,this))
}},deActivateResizeListener:function(){if(typeof this.resizeEventName!=="undefined"){D(window).off(this.resizeEventName)
}},contextMenuAttach:function(G){var F="[id='"+this.element.id+"'] ";
F+=(typeof G.options.targetSelector==="undefined")?".rf-edt-b td":G.options.targetSelector;
F=D.trim(F);
A.Event.bind(F,G.options.showEvent,D.proxy(G.__showHandler,G),G)
},contextMenuShow:function(I,G){var H=G.target;
while(this.tbodies.index(H.parentNode)==-1){H=H.parentNode
}var F=H.rowIndex;
if(!this.ranges.contains(F)){this.selectionClickListener(G)
}}});
var E=A.ui.ExtendedDataTable.$super
}(RichFaces.jQuery,window.RichFaces));;(function(C){C.hotkeys={version:"0.8",specialKeys:{8:"backspace",9:"tab",13:"return",16:"shift",17:"ctrl",18:"alt",19:"pause",20:"capslock",27:"esc",32:"space",33:"pageup",34:"pagedown",35:"end",36:"home",37:"left",38:"up",39:"right",40:"down",45:"insert",46:"del",96:"0",97:"1",98:"2",99:"3",100:"4",101:"5",102:"6",103:"7",104:"8",105:"9",106:"*",107:"+",109:"-",110:".",111:"/",112:"f1",113:"f2",114:"f3",115:"f4",116:"f5",117:"f6",118:"f7",119:"f8",120:"f9",121:"f10",122:"f11",123:"f12",144:"numlock",145:"scroll",191:"/",224:"meta"},shiftNums:{"`":"~","1":"!","2":"@","3":"#","4":"$","5":"%","6":"^","7":"&","8":"*","9":"(","0":")","-":"_","=":"+",";":": ","'":'"',",":"<",".":">","/":"?","\\":"|"}};
var A={key:"",enabledInInput:false};
function B(F){var E=(typeof F.data=="string")?{key:F.data}:F.data;
E=C.extend({},A,E);
var D=F.handler,G=E.key.toLowerCase().split(" ");
if(G.length===1&&G[0]===""){return 
}F.handler=function(H){var N=String.fromCharCode(H.which).toLowerCase(),J=(/textarea|select/i.test(H.target.nodeName)||H.target.type==="text");
if(this!==H.target&&J&&!E.enabledInInput){return 
}var O=H.type!=="keypress"&&C.hotkeys.specialKeys[H.which],P,K="",L={};
if(H.altKey&&O!=="alt"){K+="alt+"
}if(H.ctrlKey&&O!=="ctrl"){K+="ctrl+"
}if(H.metaKey&&!H.ctrlKey&&O!=="meta"){K+="meta+"
}if(H.shiftKey&&O!=="shift"){K+="shift+"
}if(O){L[K+O]=true
}else{L[K+N]=true;
L[K+C.hotkeys.shiftNums[N]]=true;
if(K==="shift+"){L[C.hotkeys.shiftNums[N]]=true
}}for(var M=0,I=G.length;
M<I;
M++){if(L[G[M]]){return D.apply(this,arguments)
}}}
}C.each(["keydown","keyup","keypress"],function(){C.event.special[this]={add:B}
})
})(jQuery);;(function(D,C){C.ui=C.ui||{};
var A={interval:1000,minValue:0,maxValue:100};
var B={initial:"> .rf-pb-init",progress:"> .rf-pb-rmng",finish:"> .rf-pb-fin"};
C.ui.ProgressBar=function(G,F){E.constructor.call(this,G);
this.__elt=this.attachToDom();
this.options=D.extend(this.options,A,F||{});
this.enabled=this.options.enabled;
this.minValue=this.options.minValue;
this.maxValue=this.options.maxValue;
this.__setValue(this.options.value||this.options.minValue);
if(this.options.resource){this.__poll()
}else{if(this.options.submitFunction){this.submitFunction=new Function("beforeUpdateHandler","afterUpdateHandler","params","event",this.options.submitFunction);
this.__poll()
}}if(this.options.onfinish){C.Event.bind(this.__elt,"finish",new Function("event",this.options.onfinish))
}};
C.BaseComponent.extend(C.ui.ProgressBar);
var E=C.ui.ProgressBar.$super;
D.extend(C.ui.ProgressBar.prototype,(function(){return{name:"ProgressBar",__isInitialState:function(){return parseFloat(this.value)<parseFloat(this.getMinValue())
},__isProgressState:function(){return !this.__isInitialState()&&!this.__isFinishState()
},__isFinishState:function(){return parseFloat(this.value)>=parseFloat(this.getMaxValue())
},__beforeUpdate:function(F){if(F.componentData&&typeof F.componentData[this.id]!="undefined"){this.setValue(F.componentData[this.id])
}},__afterUpdate:function(F){this.__poll()
},__onResourceDataAvailable:function(F){var G=C.parseJSON(F);
if(G instanceof Number||typeof G=="number"){this.setValue(G)
}this.__poll()
},__submit:function(){if(this.submitFunction){this.submitFunction.call(this,D.proxy(this.__beforeUpdate,this),D.proxy(this.__afterUpdate,this),this.__params||{})
}else{D.get(this.options.resource,this.__params||{},D.proxy(this.__onResourceDataAvailable,this),"text")
}},__poll:function(F){if(this.enabled){if(F){this.__submit()
}else{this.__pollTimer=setTimeout(D.proxy(this.__submit,this),this.options.interval)
}}},__calculatePercent:function(G){var H=parseFloat(this.getMinValue());
var F=parseFloat(this.getMaxValue());
var I=parseFloat(G);
if(H<I&&I<F){return(100*(I-H))/(F-H)
}else{if(I<=H){return 0
}else{if(I>=F){return 100
}}}},__getPropertyOrObject:function(G,F){if(D.isPlainObject(G)&&G.propName){return G.propName
}return G
},getValue:function(){return this.value
},__showState:function(F){var G=D(B[F],this.__elt);
if(G.length==0&&(F=="initial"||F=="finish")){G=D(B.progress,this.__elt)
}G.show().siblings().hide()
},__setValue:function(G,F){this.value=parseFloat(this.__getPropertyOrObject(G,"value"));
if(this.__isFinishState()||this.__isInitialState()){this.disable()
}},__updateVisualState:function(){if(this.__isInitialState()){this.__showState("initial")
}else{if(this.__isFinishState()){this.__showState("finish")
}else{this.__showState("progress")
}}var F=this.__calculatePercent(this.value);
D(".rf-pb-prgs",this.__elt).css("width",F+"%")
},setValue:function(G){var F=this.__isFinishState();
this.__setValue(G);
this.__updateVisualState();
if(!F&&this.__isFinishState()){C.Event.callHandler(this.__elt,"finish")
}},getMaxValue:function(){return this.maxValue
},getMinValue:function(){return this.minValue
},isAjaxMode:function(){return !!this.submitFunction||!!this.options.resource
},disable:function(){this.__params=null;
if(this.__pollTimer){clearTimeout(this.__pollTimer);
this.__pollTimer=null
}this.enabled=false
},enable:function(F){if(this.isEnabled()){return 
}this.__params=F;
this.enabled=true;
if(this.isAjaxMode()){this.__poll(true)
}},isEnabled:function(){return this.enabled
},destroy:function(){this.disable();
this.__elt=null;
E.destroy.call(this)
}}
}()))
})(RichFaces.jQuery,RichFaces);;(function(B,A){A.ui=A.ui||{};
A.ui.ComponentControl=A.ui.ComponentControl||{};
B.extend(A.ui.ComponentControl,{execute:function(H,G){var I=G.target;
var D=G.selector;
var J=G.callback;
if(G.onbeforeoperation&&typeof G.onbeforeoperation=="function"){var C=G.onbeforeoperation(H);
if(C=="false"||C==0){return 
}}if(I){for(var F=0;
F<I.length;
F++){var E=document.getElementById(I[F]);
if(E){A.ui.ComponentControl.invokeOnComponent(H,E,J)
}}}if(D){A.ui.ComponentControl.invokeOnComponent(H,D,J)
}},invokeOnComponent:function(C,D,E){if(E&&typeof E=="function"){B(D).each(function(){var F=A.component(this);
if(F){E(C,F)
}})
}}})
})(RichFaces.jQuery,window.RichFaces);;(function(D,A){A.ui=A.ui||{};
var C=function(K,I,G){var M;
var J=function(N){N.data.fn.call(N.data.component,N)
};
var L={};
L.component=G;
for(M in K){var H=D(document.getElementById(M));
L.id=M;
L.page=K[M];
L.element=H;
L.fn=G.processClick;
H.bind("click",F(L),J)
}};
var F=function(I){var G;
var H={};
for(G in I){H[G]=I[G]
}return H
};
var B=function(G,H){if(H.type=="mousedown"){G.addClass("rf-ds-press")
}else{if(H.type=="mouseup"||H.type=="mouseout"){G.removeClass("rf-ds-press")
}}};
A.ui.DataScroller=function(K,J,G){E.constructor.call(this,K);
var I=this.attachToDom();
this.options=G;
this.currentPage=G.currentPage;
if(J&&typeof J=="function"){RichFaces.Event.bindById(K,this.getScrollEventName(),J)
}var H={};
if(G.buttons){D(I).delegate(".rf-ds-btn","mouseup mousedown mouseout",function(L){if(D(this).hasClass("rf-ds-dis")){D(this).removeClass("rf-ds-press")
}else{B(D(this),L)
}});
C(G.buttons.left,H,this);
C(G.buttons.right,H,this)
}if(G.digitals){D(I).delegate(".rf-ds-nmb-btn","mouseup mousedown mouseout",function(L){B(D(this),L)
});
C(G.digitals,H,this)
}};
A.BaseComponent.extend(A.ui.DataScroller);
var E=A.ui.DataScroller.$super;
D.extend(A.ui.DataScroller.prototype,(function(){var G="rich:datascroller:onscroll";
return{name:"RichFaces.ui.DataScroller",processClick:function(H){var J=H.data;
if(J){var I=J.page;
if(I){this.switchToPage(I)
}}},switchToPage:function(H){if(typeof H!="undefined"&&H!=null){RichFaces.Event.fireById(this.id,this.getScrollEventName(),{page:H})
}},fastForward:function(){this.switchToPage("fastforward")
},fastRewind:function(){this.switchToPage("fastrewind")
},next:function(){this.switchToPage("next")
},previous:function(){this.switchToPage("previous")
},first:function(){this.switchToPage("first")
},last:function(){this.switchToPage("last")
},getScrollEventName:function(){return G
},destroy:function(){E.destroy.call(this)
}}
})())
})(RichFaces.jQuery,window.RichFaces);;(function(E,C){C.ui=C.ui||{};
C.ui.Message=function(I,H){G.constructor.call(this,I,H,A);
if(this.options.isMessages){this.severityClasses=["rf-msgs-inf","rf-msgs-wrn","rf-msgs-err","rf-msgs-ftl"];
this.summaryClass="rf-msgs-sum";
this.detailClass="rf-msgs-det"
}else{this.severityClasses=["rf-msg-inf","rf-msg-wrn","rf-msg-err","rf-msg-ftl"];
this.summaryClass="rf-msg-sum";
this.detailClass="rf-msg-det"
}};
C.ui.Base.extend(C.ui.Message);
var G=C.ui.Message.$super;
var A={showSummary:true,level:0,isMessages:false,globalOnly:false};
var F=function(K,H,M){var J=E(C.getDomElement(this.id));
var L=M.sourceId;
var I=M.message;
if(!this.options.forComponentId){if(!I||this.options.globalOnly){var H;
while(H=C.getDomElement(this.id+":"+L)){E(H).remove()
}}else{D.call(this,L,I)
}}else{if(this.options.forComponentId===L){J.empty();
D.call(this,L,I)
}}};
var D=function(H,J){if(J&&J.severity>=this.options.level){var I=E(C.getDomElement(this.id));
var K=E("<span/>",{"class":(this.severityClasses)[J.severity],id:this.id+":"+H});
if(J.summary){if(this.options.tooltip){K.attr("title",J.summary)
}else{if(this.options.showSummary){K.append(E("<span/>",{"class":(this.summaryClass)}).text(J.summary))
}}}if(this.options.showDetail&&J.detail){K.append(E("<span/>",{"class":(this.detailClass)}).text(J.detail))
}I.append(K)
}};
var B=function(){C.Event.bind(window.document,C.Event.MESSAGE_EVENT_TYPE+this.namespace,F,this)
};
E.extend(C.ui.Message.prototype,{name:"Message",__bindEventHandlers:B,destroy:function(){C.Event.unbind(window.document,C.Event.MESSAGE_EVENT_TYPE+this.namespace);
G.destroy.call(this)
}})
})(RichFaces.jQuery,window.RichFaces||(window.RichFaces={}));;(function(C,B){B.ui=B.ui||{};
var A={mode:"server",cssRoot:"ddm",cssClasses:{}};
B.ui.MenuItem=function(G,F){this.options={};
C.extend(this.options,A,F||{});
D.constructor.call(this,G);
C.extend(this.options.cssClasses,E.call(this,this.options.cssRoot));
this.attachToDom(G);
this.element=C(B.getDomElement(G));
B.Event.bindById(this.id,"click",this.__clickHandler,this);
B.Event.bindById(this.id,"mouseenter",this.select,this);
B.Event.bindById(this.id,"mouseleave",this.unselect,this);
this.selected=false
};
var E=function(G){var F={itemCss:"rf-"+G+"-itm",selectItemCss:"rf-"+G+"-itm-sel",unselectItemCss:"rf-"+G+"-itm-unsel",labelCss:"rf-"+G+"-lbl"};
return F
};
B.BaseComponent.extend(B.ui.MenuItem);
var D=B.ui.MenuItem.$super;
C.extend(B.ui.MenuItem.prototype,(function(){return{name:"MenuItem",select:function(){this.element.removeClass(this.options.cssClasses.unselectItemCss);
this.element.addClass(this.options.cssClasses.selectItemCss);
this.selected=true
},unselect:function(){this.element.removeClass(this.options.cssClasses.selectItemCss);
this.element.addClass(this.options.cssClasses.unselectItemCss);
this.selected=false
},activate:function(){this.invokeEvent("click",B.getDomElement(this.id))
},isSelected:function(){return this.selected
},__clickHandler:function(I){if(C(I.target).is(":input:not(:button):not(:reset):not(:submit)")){return 
}var F=this.__getParentMenu();
if(F){F.processItem(this.element)
}var H=B.getDomElement(this.id);
var K=this.options.params;
var G=this.__getParentForm(H);
var J={};
J[H.id]=H.id;
C.extend(J,K||{});
I.form=G;
I.itemId=J;
this.options.onClickHandler.call(this,I)
},__getParentForm:function(F){return C(C(F).parents("form").get(0))
},__getParentMenu:function(){var F=this.element.parents("div."+this.options.cssClasses.labelCss);
if(F&&F.length>0){return B.component(F)
}else{return null
}}}
})())
})(RichFaces.jQuery,RichFaces);;(function(A,B){if(typeof define==="function"&&define.amd){define(B)
}else{A.atmosphere=B()
}}(this,function(){var C="2.2.5-javascript",A={},D,G=[],F=[],E=0,B=Object.prototype.hasOwnProperty;
A={onError:function(H){},onClose:function(H){},onOpen:function(H){},onReopen:function(H){},onMessage:function(H){},onReconnect:function(I,H){},onMessagePublished:function(H){},onTransportFailure:function(I,H){},onLocalMessage:function(H){},onFailureToReconnect:function(I,H){},onClientTimeout:function(H){},onOpenAfterResume:function(H){},WebsocketApiAdapter:function(I){var H,J;
I.onMessage=function(K){J.onmessage({data:K.responseBody})
};
I.onMessagePublished=function(K){J.onmessage({data:K.responseBody})
};
I.onOpen=function(K){J.onopen(K)
};
J={close:function(){H.close()
},send:function(K){H.push(K)
},onmessage:function(K){},onopen:function(K){},onclose:function(K){},onerror:function(K){}};
H=new A.subscribe(I);
return J
},AtmosphereRequest:function(z){var P={timeout:300000,method:"GET",headers:{},contentType:"",callback:null,url:"",data:"",suspend:true,maxRequest:-1,reconnect:true,maxStreamingLength:10000000,lastIndex:0,logLevel:"info",requestCount:0,fallbackMethod:"GET",fallbackTransport:"streaming",transport:"long-polling",webSocketImpl:null,webSocketBinaryType:null,dispatchUrl:null,webSocketPathDelimiter:"@@",enableXDR:false,rewriteURL:false,attachHeadersAsQueryString:true,executeCallbackBeforeReconnect:false,readyState:0,withCredentials:false,trackMessageLength:false,messageDelimiter:"|",connectTimeout:-1,reconnectInterval:0,dropHeaders:true,uuid:0,async:true,shared:false,readResponsesHeaders:false,maxReconnectOnClose:5,enableProtocol:true,pollingInterval:0,heartbeat:{client:null,server:null},ackInterval:0,closeAsync:false,reconnectOnServerError:true,onError:function(Ac){},onClose:function(Ac){},onOpen:function(Ac){},onMessage:function(Ac){},onReopen:function(Ad,Ac){},onReconnect:function(Ad,Ac){},onMessagePublished:function(Ac){},onTransportFailure:function(Ad,Ac){},onLocalMessage:function(Ac){},onFailureToReconnect:function(Ad,Ac){},onClientTimeout:function(Ac){},onOpenAfterResume:function(Ac){}};
var AO={status:200,reasonPhrase:"OK",responseBody:"",messages:[],headers:[],state:"messageReceived",transport:"polling",error:null,request:null,partialMessage:"",errorHandled:false,closedByClientTimeout:false,ffTryingReconnect:false};
var AS=null;
var AC=null;
var Y=null;
var N=null;
var u=null;
var U=true;
var AU=0;
var AG=" ";
var AL=false;
var n=null;
var H;
var AT=null;
var o=A.util.now();
var X;
var Ab;
AK(z);
function AF(){U=true;
AL=false;
AU=0;
AS=null;
AC=null;
Y=null;
N=null
}function r(){K();
AF()
}function g(Ad,Ac){if(AO.partialMessage===""&&(Ac.transport==="streaming")&&(Ad.responseText.length>Ac.maxStreamingLength)){return true
}return false
}function b(){if(P.enableProtocol&&!P.firstMessage){var Ae="X-Atmosphere-Transport=close&X-Atmosphere-tracking-id="+P.uuid;
A.util.each(P.headers,function(Ag,Ai){var Ah=A.util.isFunction(Ai)?Ai.call(this,P,P,AO):Ai;
if(Ah!=null){Ae+="&"+encodeURIComponent(Ag)+"="+encodeURIComponent(Ah)
}});
var Ac=P.url.replace(/([?&])_=[^&]*/,Ae);
Ac=Ac+(Ac===P.url?(/\?/.test(P.url)?"&":"?")+Ae:"");
var Ad={connected:false};
var Af=new A.AtmosphereRequest(Ad);
Af.attachHeadersAsQueryString=false;
Af.dropHeaders=true;
Af.url=Ac;
Af.contentType="text/plain";
Af.transport="polling";
Af.method="GET";
Af.data="";
if(P.enableXDR){Af.enableXDR=P.enableXDR
}Af.async=Ad.closeAsync;
AI("",Af)
}}function f(){if(P.logLevel==="debug"){A.util.debug("Closing")
}AL=true;
if(P.reconnectId){clearTimeout(P.reconnectId);
delete P.reconnectId
}if(P.heartbeatTimer){clearTimeout(P.heartbeatTimer)
}P.reconnect=false;
AO.request=P;
AO.state="unsubscribe";
AO.responseBody="";
AO.status=408;
AO.partialMessage="";
AE();
b();
K()
}function K(){AO.partialMessage="";
if(P.id){clearTimeout(P.id)
}if(P.heartbeatTimer){clearTimeout(P.heartbeatTimer)
}if(N!=null){N.close();
N=null
}if(u!=null){u.abort();
u=null
}if(Y!=null){Y.abort();
Y=null
}if(AS!=null){if(AS.canSendMessage){AS.close()
}AS=null
}if(AC!=null){AC.close();
AC=null
}AD()
}function AD(){if(H!=null){clearInterval(X);
document.cookie=Ab+"=; expires=Thu, 01 Jan 1970 00:00:00 GMT; path=/";
H.signal("close",{reason:"",heir:!AL?o:(H.get("children")||[])[0]});
H.close()
}if(AT!=null){AT.close()
}}function AK(Ac){r();
P=A.util.extend(P,Ac);
P.mrequest=P.reconnect;
if(!P.reconnect){P.reconnect=true
}}function AQ(){return P.webSocketImpl!=null||window.WebSocket||window.MozWebSocket
}function AP(){return window.EventSource
}function w(){if(P.shared){AT=AZ(P);
if(AT!=null){if(P.logLevel==="debug"){A.util.debug("Storage service available. All communication will be local")
}if(AT.open(P)){return 
}}if(P.logLevel==="debug"){A.util.debug("No Storage service available.")
}AT=null
}P.firstMessage=E==0?true:false;
P.isOpen=false;
P.ctime=A.util.now();
if(P.uuid===0){P.uuid=E
}AO.closedByClientTimeout=false;
if(P.transport!=="websocket"&&P.transport!=="sse"){i(P)
}else{if(P.transport==="websocket"){if(!AQ()){AV("Websocket is not supported, using request.fallbackTransport ("+P.fallbackTransport+")")
}else{AB(false)
}}else{if(P.transport==="sse"){if(!AP()){AV("Server Side Events(SSE) is not supported, using request.fallbackTransport ("+P.fallbackTransport+")")
}else{a(false)
}}}}}function AZ(Ag){var Ah,Af,Ak,Ac="atmosphere-"+Ag.url,Ad={storage:function(){function Al(Ap){if(Ap.key===Ac&&Ap.newValue){Ae(Ap.newValue)
}}if(!A.util.storage){return 
}var Ao=window.localStorage,Am=function(Ap){return A.util.parseJSON(Ao.getItem(Ac+"-"+Ap))
},An=function(Ap,Aq){Ao.setItem(Ac+"-"+Ap,A.util.stringifyJSON(Aq))
};
return{init:function(){An("children",Am("children").concat([o]));
A.util.on(window,"storage",Al);
return Am("opened")
},signal:function(Ap,Aq){Ao.setItem(Ac,A.util.stringifyJSON({target:"p",type:Ap,data:Aq}))
},close:function(){var Ap=Am("children");
A.util.off(window,"storage",Al);
if(Ap){if(Ai(Ap,Ag.id)){An("children",Ap)
}}}}
},windowref:function(){var Al=window.open("",Ac.replace(/\W/g,""));
if(!Al||Al.closed||!Al.callbacks){return 
}return{init:function(){Al.callbacks.push(Ae);
Al.children.push(o);
return Al.opened
},signal:function(Am,An){if(!Al.closed&&Al.fire){Al.fire(A.util.stringifyJSON({target:"p",type:Am,data:An}))
}},close:function(){if(!Ak){Ai(Al.callbacks,Ae);
Ai(Al.children,o)
}}}
}};
function Ai(Ao,An){var Al,Am=Ao.length;
for(Al=0;
Al<Am;
Al++){if(Ao[Al]===An){Ao.splice(Al,1)
}}return Am!==Ao.length
}function Ae(Al){var An=A.util.parseJSON(Al),Am=An.data;
if(An.target==="c"){switch(An.type){case"open":s("opening","local",P);
break;
case"close":if(!Ak){Ak=true;
if(Am.reason==="aborted"){f()
}else{if(Am.heir===o){w()
}else{setTimeout(function(){w()
},100)
}}}break;
case"message":L(Am,"messageReceived",200,Ag.transport);
break;
case"localMessage":d(Am);
break
}}}function Aj(){var Al=new RegExp("(?:^|; )("+encodeURIComponent(Ac)+")=([^;]*)").exec(document.cookie);
if(Al){return A.util.parseJSON(decodeURIComponent(Al[2]))
}}Ah=Aj();
if(!Ah||A.util.now()-Ah.ts>1000){return 
}Af=Ad.storage()||Ad.windowref();
if(!Af){return 
}return{open:function(){var Al;
X=setInterval(function(){var Am=Ah;
Ah=Aj();
if(!Ah||Am.ts===Ah.ts){Ae(A.util.stringifyJSON({target:"c",type:"close",data:{reason:"error",heir:Am.heir}}))
}},1000);
Al=Af.init();
if(Al){setTimeout(function(){s("opening","local",Ag)
},50)
}return Al
},send:function(Al){Af.signal("send",Al)
},localSend:function(Al){Af.signal("localSend",A.util.stringifyJSON({id:o,event:Al}))
},close:function(){if(!AL){clearInterval(X);
Af.signal("close");
Af.close()
}}}
}function Aa(){var Ad,Ac="atmosphere-"+P.url,Ah={storage:function(){function Ai(Ak){if(Ak.key===Ac&&Ak.newValue){Ae(Ak.newValue)
}}if(!A.util.storage){return 
}var Aj=window.localStorage;
return{init:function(){A.util.on(window,"storage",Ai)
},signal:function(Ak,Al){Aj.setItem(Ac,A.util.stringifyJSON({target:"c",type:Ak,data:Al}))
},get:function(Ak){return A.util.parseJSON(Aj.getItem(Ac+"-"+Ak))
},set:function(Ak,Al){Aj.setItem(Ac+"-"+Ak,A.util.stringifyJSON(Al))
},close:function(){A.util.off(window,"storage",Ai);
Aj.removeItem(Ac);
Aj.removeItem(Ac+"-opened");
Aj.removeItem(Ac+"-children")
}}
},windowref:function(){var Aj=Ac.replace(/\W/g,""),Ai=document.getElementById(Aj),Ak;
if(!Ai){Ai=document.createElement("div");
Ai.id=Aj;
Ai.style.display="none";
Ai.innerHTML='<iframe name="'+Aj+'" />';
document.body.appendChild(Ai)
}Ak=Ai.firstChild.contentWindow;
return{init:function(){Ak.callbacks=[Ae];
Ak.fire=function(Al){var Am;
for(Am=0;
Am<Ak.callbacks.length;
Am++){Ak.callbacks[Am](Al)
}}
},signal:function(Al,Am){if(!Ak.closed&&Ak.fire){Ak.fire(A.util.stringifyJSON({target:"c",type:Al,data:Am}))
}},get:function(Al){return !Ak.closed?Ak[Al]:null
},set:function(Al,Am){if(!Ak.closed){Ak[Al]=Am
}},close:function(){}}
}};
function Ae(Ai){var Ak=A.util.parseJSON(Ai),Aj=Ak.data;
if(Ak.target==="p"){switch(Ak.type){case"send":T(Aj);
break;
case"localSend":d(Aj);
break;
case"close":f();
break
}}}n=function Ag(Ai){Ad.signal("message",Ai)
};
function Af(){document.cookie=Ab+"="+encodeURIComponent(A.util.stringifyJSON({ts:A.util.now()+1,heir:(Ad.get("children")||[])[0]}))+"; path=/"
}Ad=Ah.storage()||Ah.windowref();
Ad.init();
if(P.logLevel==="debug"){A.util.debug("Installed StorageService "+Ad)
}Ad.set("children",[]);
if(Ad.get("opened")!=null&&!Ad.get("opened")){Ad.set("opened",false)
}Ab=encodeURIComponent(Ac);
Af();
X=setInterval(Af,1000);
H=Ad
}function s(Ae,Ah,Ad){if(P.shared&&Ah!=="local"){Aa()
}if(H!=null){H.set("opened",true)
}Ad.close=function(){f()
};
if(AU>0&&Ae==="re-connecting"){Ad.isReopen=true;
Q(AO)
}else{if(AO.error==null){AO.request=Ad;
var Af=AO.state;
AO.state=Ae;
var Ac=AO.transport;
AO.transport=Ah;
var Ag=AO.responseBody;
AE();
AO.responseBody=Ag;
AO.state=Af;
AO.transport=Ac
}}}function AX(Ae){Ae.transport="jsonp";
var Ad=P,Ac;
if((Ae!=null)&&(typeof (Ae)!=="undefined")){Ad=Ae
}u={open:function(){var Ag="atmosphere"+(++o);
function Af(){var Ah=Ad.url;
if(Ad.dispatchUrl!=null){Ah+=Ad.dispatchUrl
}var Aj=Ad.data;
if(Ad.attachHeadersAsQueryString){Ah=O(Ad);
if(Aj!==""){Ah+="&X-Atmosphere-Post-Body="+encodeURIComponent(Aj)
}Aj=""
}var Ai=document.head||document.getElementsByTagName("head")[0]||document.documentElement;
Ac=document.createElement("script");
Ac.src=Ah+"&jsonpTransport="+Ag;
Ac.clean=function(){Ac.clean=Ac.onerror=Ac.onload=Ac.onreadystatechange=null;
if(Ac.parentNode){Ac.parentNode.removeChild(Ac)
}};
Ac.onload=Ac.onreadystatechange=function(){if(!Ac.readyState||/loaded|complete/.test(Ac.readyState)){Ac.clean()
}};
Ac.onerror=function(){Ac.clean();
Ad.lastIndex=0;
if(Ad.openId){clearTimeout(Ad.openId)
}if(Ad.heartbeatTimer){clearTimeout(Ad.heartbeatTimer)
}if(Ad.reconnect&&AU++<Ad.maxReconnectOnClose){s("re-connecting",Ad.transport,Ad);
AJ(u,Ad,Ae.reconnectInterval);
Ad.openId=setTimeout(function(){v(Ad)
},Ad.reconnectInterval+1000)
}else{q(0,"maxReconnectOnClose reached")
}};
Ai.insertBefore(Ac,Ai.firstChild)
}window[Ag]=function(Aj){if(Ad.reconnect){if(Ad.maxRequest===-1||Ad.requestCount++<Ad.maxRequest){J(Ad);
if(!Ad.executeCallbackBeforeReconnect){AJ(u,Ad,Ad.pollingInterval)
}if(Aj!=null&&typeof Aj!=="string"){try{Aj=Aj.message
}catch(Ai){}}var Ah=R(Aj,Ad,AO);
if(!Ah){L(AO.responseBody,"messageReceived",200,Ad.transport)
}if(Ad.executeCallbackBeforeReconnect){AJ(u,Ad,Ad.pollingInterval)
}}else{A.util.log(P.logLevel,["JSONP reconnect maximum try reached "+P.requestCount]);
q(0,"maxRequest reached")
}}};
setTimeout(function(){Af()
},50)
},abort:function(){if(Ac&&Ac.clean){Ac.clean()
}}};
u.open()
}function AR(Ac){if(P.webSocketImpl!=null){return P.webSocketImpl
}else{if(window.WebSocket){return new WebSocket(Ac)
}else{return new MozWebSocket(Ac)
}}}function V(){return O(P,A.util.getAbsoluteURL(P.webSocketUrl||P.url)).replace(/^http/,"ws")
}function p(){var Ac=O(P);
return Ac
}function a(Ad){AO.transport="sse";
var Ac=p();
if(P.logLevel==="debug"){A.util.debug("Invoking executeSSE");
A.util.debug("Using URL: "+Ac)
}if(Ad&&!P.reconnect){if(AC!=null){K()
}return 
}try{AC=new EventSource(Ac,{withCredentials:P.withCredentials})
}catch(Ae){q(0,Ae);
AV("SSE failed. Downgrading to fallback transport and resending");
return 
}if(P.connectTimeout>0){P.id=setTimeout(function(){if(!Ad){K()
}},P.connectTimeout)
}AC.onopen=function(Af){J(P);
if(P.logLevel==="debug"){A.util.debug("SSE successfully opened")
}if(!P.enableProtocol){if(!Ad){s("opening","sse",P)
}else{s("re-opening","sse",P)
}}else{if(P.isReopen){P.isReopen=false;
s("re-opening",P.transport,P)
}}Ad=true;
if(P.method==="POST"){AO.state="messageReceived";
AC.send(P.data)
}};
AC.onmessage=function(Ag){J(P);
if(!P.enableXDR&&Ag.origin&&Ag.origin!==window.location.protocol+"//"+window.location.host){A.util.log(P.logLevel,["Origin was not "+window.location.protocol+"//"+window.location.host]);
return 
}AO.state="messageReceived";
AO.status=200;
Ag=Ag.data;
var Af=R(Ag,P,AO);
if(!Af){AE();
AO.responseBody="";
AO.messages=[]
}};
AC.onerror=function(Af){clearTimeout(P.id);
if(P.heartbeatTimer){clearTimeout(P.heartbeatTimer)
}if(AO.closedByClientTimeout){return 
}AA(Ad);
K();
if(AL){A.util.log(P.logLevel,["SSE closed normally"])
}else{if(!Ad){AV("SSE failed. Downgrading to fallback transport and resending")
}else{if(P.reconnect&&(AO.transport==="sse")){if(AU++<P.maxReconnectOnClose){s("re-connecting",P.transport,P);
if(P.reconnectInterval>0){P.reconnectId=setTimeout(function(){a(true)
},P.reconnectInterval)
}else{a(true)
}AO.responseBody="";
AO.messages=[]
}else{A.util.log(P.logLevel,["SSE reconnect maximum try reached "+AU]);
q(0,"maxReconnectOnClose reached")
}}}}}
}function AB(Ad){AO.transport="websocket";
var Ac=V(P.url);
if(P.logLevel==="debug"){A.util.debug("Invoking executeWebSocket");
A.util.debug("Using URL: "+Ac)
}if(Ad&&!P.reconnect){if(AS!=null){K()
}return 
}AS=AR(Ac);
if(P.webSocketBinaryType!=null){AS.binaryType=P.webSocketBinaryType
}if(P.connectTimeout>0){P.id=setTimeout(function(){if(!Ad){var Ag={code:1002,reason:"",wasClean:false};
AS.onclose(Ag);
try{K()
}catch(Ah){}return 
}},P.connectTimeout)
}AS.onopen=function(Ah){J(P);
if(P.logLevel==="debug"){A.util.debug("Websocket successfully opened")
}var Ag=Ad;
if(AS!=null){AS.canSendMessage=true
}if(!P.enableProtocol){Ad=true;
if(Ag){s("re-opening","websocket",P)
}else{s("opening","websocket",P)
}}if(AS!=null){if(P.method==="POST"){AO.state="messageReceived";
AS.send(P.data)
}}};
AS.onmessage=function(Ai){J(P);
if(P.enableProtocol){Ad=true
}AO.state="messageReceived";
AO.status=200;
Ai=Ai.data;
var Ag=typeof (Ai)==="string";
if(Ag){var Ah=R(Ai,P,AO);
if(!Ah){AE();
AO.responseBody="";
AO.messages=[]
}}else{Ai=S(P,Ai);
if(Ai===""){return 
}AO.responseBody=Ai;
AE();
AO.responseBody=null
}};
AS.onerror=function(Ag){clearTimeout(P.id);
if(P.heartbeatTimer){clearTimeout(P.heartbeatTimer)
}};
AS.onclose=function(Ag){clearTimeout(P.id);
if(AO.state==="closed"){return 
}var Ah=Ag.reason;
if(Ah===""){switch(Ag.code){case 1000:Ah="Normal closure; the connection successfully completed whatever purpose for which it was created.";
break;
case 1001:Ah="The endpoint is going away, either because of a server failure or because the browser is navigating away from the page that opened the connection.";
break;
case 1002:Ah="The endpoint is terminating the connection due to a protocol error.";
break;
case 1003:Ah="The connection is being terminated because the endpoint received data of a type it cannot accept (for example, a text-only endpoint received binary data).";
break;
case 1004:Ah="The endpoint is terminating the connection because a data frame was received that is too large.";
break;
case 1005:Ah="Unknown: no status code was provided even though one was expected.";
break;
case 1006:Ah="Connection was closed abnormally (that is, with no close frame being sent).";
break
}}if(P.logLevel==="warn"){A.util.warn("Websocket closed, reason: "+Ah);
A.util.warn("Websocket closed, wasClean: "+Ag.wasClean)
}if(AO.closedByClientTimeout){return 
}AA(Ad);
AO.state="closed";
if(AL){A.util.log(P.logLevel,["Websocket closed normally"])
}else{if(!Ad){AV("Websocket failed. Downgrading to Comet and resending")
}else{if(P.reconnect&&AO.transport==="websocket"&&Ag.code!==1001){K();
if(AU++<P.maxReconnectOnClose){s("re-connecting",P.transport,P);
if(P.reconnectInterval>0){P.reconnectId=setTimeout(function(){AO.responseBody="";
AO.messages=[];
AB(true)
},P.reconnectInterval)
}else{AO.responseBody="";
AO.messages=[];
AB(true)
}}else{A.util.log(P.logLevel,["Websocket reconnect maximum try reached "+P.requestCount]);
if(P.logLevel==="warn"){A.util.warn("Websocket error, reason: "+Ag.reason)
}q(0,"maxReconnectOnClose reached")
}}}}};
var Ae=navigator.userAgent.toLowerCase();
var Af=Ae.indexOf("android")>-1;
if(Af&&AS.url===undefined){AS.onclose({reason:"Android 4.1 does not support websockets.",wasClean:false})
}}function S(Ai,Ah){var Ag=Ah;
if(Ai.transport==="polling"){return Ag
}if(A.util.trim(Ah).length!==0&&Ai.enableProtocol&&Ai.firstMessage){var Aj=Ai.trackMessageLength?1:0;
var Af=Ah.split(Ai.messageDelimiter);
if(Af.length<=Aj+1){return Ag
}Ai.firstMessage=false;
Ai.uuid=A.util.trim(Af[Aj]);
if(Af.length<=Aj+2){A.util.log("error",["Protocol data not sent by the server. If you enable protocol on client side, be sure to install JavascriptProtocol interceptor on server side.Also note that atmosphere-runtime 2.2+ should be used."])
}var Ac=parseInt(A.util.trim(Af[Aj+1]),10);
AG=Af[Aj+2];
if(!isNaN(Ac)&&Ac>0){var Ae=function(){T(AG);
Ai.heartbeatTimer=setTimeout(Ae,Ac)
};
Ai.heartbeatTimer=setTimeout(Ae,Ac)
}if(Ai.transport!=="long-polling"){v(Ai)
}E=Ai.uuid;
Ag="";
Aj=Ai.trackMessageLength?4:3;
if(Af.length>Aj+1){for(var Ad=Aj;
Ad<Af.length;
Ad++){Ag+=Af[Ad];
if(Ad+1!==Af.length){Ag+=Ai.messageDelimiter
}}}if(Ai.ackInterval!==0){setTimeout(function(){T("...ACK...")
},Ai.ackInterval)
}}else{if(Ai.enableProtocol&&Ai.firstMessage&&A.util.browser.msie&&+A.util.browser.version.split(".")[0]<10){A.util.log(P.logLevel,["Receiving unexpected data from IE"])
}else{v(Ai)
}}return Ag
}function J(Ac){Ac.timedOut=false;
clearTimeout(Ac.id);
if(Ac.timeout>0&&Ac.transport!=="polling"){Ac.id=setTimeout(function(){Ac.timedOut=true;
AY(Ac);
b();
K()
},Ac.timeout)
}}function AY(Ac){AO.closedByClientTimeout=true;
AO.state="closedByClient";
AO.responseBody="";
AO.status=408;
AO.messages=[];
AE()
}function q(Ac,Ad){K();
clearTimeout(P.id);
AO.state="error";
AO.reasonPhrase=Ad;
AO.responseBody="";
AO.status=Ac;
AO.messages=[];
AE()
}function R(Ag,Af,Ac){Ag=S(Af,Ag);
if(Ag.length===0){return true
}Ac.responseBody=Ag;
if(Af.trackMessageLength){Ag=Ac.partialMessage+Ag;
var Ae=[];
var Ad=Ag.indexOf(Af.messageDelimiter);
while(Ad!==-1){var Ai=Ag.substring(0,Ad);
var Ah=+Ai;
if(isNaN(Ah)){throw new Error('message length "'+Ai+'" is not a number')
}Ad+=Af.messageDelimiter.length;
if(Ad+Ah>Ag.length){Ad=-1
}else{Ae.push(Ag.substring(Ad,Ad+Ah));
Ag=Ag.substring(Ad+Ah,Ag.length);
Ad=Ag.indexOf(Af.messageDelimiter)
}}Ac.partialMessage=Ag;
if(Ae.length!==0){Ac.responseBody=Ae.join(Af.messageDelimiter);
Ac.messages=Ae;
return false
}else{Ac.responseBody="";
Ac.messages=[];
return true
}}else{Ac.responseBody=Ag
}return false
}function AV(Ac){A.util.log(P.logLevel,[Ac]);
if(typeof (P.onTransportFailure)!=="undefined"){P.onTransportFailure(Ac,P)
}else{if(typeof (A.util.onTransportFailure)!=="undefined"){A.util.onTransportFailure(Ac,P)
}}P.transport=P.fallbackTransport;
var Ad=P.connectTimeout===-1?0:P.connectTimeout;
if(P.reconnect&&P.transport!=="none"||P.transport==null){P.method=P.fallbackMethod;
AO.transport=P.fallbackTransport;
P.fallbackTransport="none";
if(Ad>0){P.reconnectId=setTimeout(function(){w()
},Ad)
}else{w()
}}else{q(500,"Unable to reconnect with fallback transport")
}}function O(Ae,Ac){var Ad=P;
if((Ae!=null)&&(typeof (Ae)!=="undefined")){Ad=Ae
}if(Ac==null){Ac=Ad.url
}if(!Ad.attachHeadersAsQueryString){return Ac
}if(Ac.indexOf("X-Atmosphere-Framework")!==-1){return Ac
}Ac+=(Ac.indexOf("?")!==-1)?"&":"?";
Ac+="X-Atmosphere-tracking-id="+Ad.uuid;
Ac+="&X-Atmosphere-Framework="+C;
Ac+="&X-Atmosphere-Transport="+Ad.transport;
if(Ad.trackMessageLength){Ac+="&X-Atmosphere-TrackMessageSize=true"
}if(Ad.heartbeat!==null&&Ad.heartbeat.server!==null){Ac+="&X-Heartbeat-Server="+Ad.heartbeat.server
}if(Ad.contentType!==""){Ac+="&Content-Type="+(Ad.transport==="websocket"?Ad.contentType:encodeURIComponent(Ad.contentType))
}if(Ad.enableProtocol){Ac+="&X-atmo-protocol=true"
}A.util.each(Ad.headers,function(Af,Ah){var Ag=A.util.isFunction(Ah)?Ah.call(this,Ad,Ae,AO):Ah;
if(Ag!=null){Ac+="&"+encodeURIComponent(Af)+"="+encodeURIComponent(Ag)
}});
return Ac
}function v(Ac){if(!Ac.isOpen){Ac.isOpen=true;
s("opening",Ac.transport,Ac)
}else{if(Ac.isReopen){Ac.isReopen=false;
s("re-opening",Ac.transport,Ac)
}else{if(AO.state==="messageReceived"&&(Ac.transport==="jsonp"||Ac.transport==="long-polling")){AM(AO)
}}}}function i(Af){var Ad=P;
if((Af!=null)||(typeof (Af)!=="undefined")){Ad=Af
}Ad.lastIndex=0;
Ad.readyState=0;
if((Ad.transport==="jsonp")||((Ad.enableXDR)&&(A.util.checkCORSSupport()))){AX(Ad);
return 
}if(A.util.browser.msie&&+A.util.browser.version.split(".")[0]<10){if((Ad.transport==="streaming")){if(Ad.enableXDR&&window.XDomainRequest){m(Ad)
}else{AW(Ad)
}return 
}if((Ad.enableXDR)&&(window.XDomainRequest)){m(Ad);
return 
}}var Ag=function(){Ad.lastIndex=0;
if(Ad.reconnect&&AU++<Ad.maxReconnectOnClose){AO.ffTryingReconnect=true;
s("re-connecting",Af.transport,Af);
AJ(Ae,Ad,Af.reconnectInterval)
}else{q(0,"maxReconnectOnClose reached")
}};
var Ac=function(){AO.errorHandled=true;
K();
Ag()
};
if(Ad.force||(Ad.reconnect&&(Ad.maxRequest===-1||Ad.requestCount++<Ad.maxRequest))){Ad.force=false;
var Ae=A.util.xhr();
Ae.hasData=false;
j(Ae,Ad,true);
if(Ad.suspend){Y=Ae
}if(Ad.transport!=="polling"){AO.transport=Ad.transport;
Ae.onabort=function(){AA(true)
};
Ae.onerror=function(){AO.error=true;
AO.ffTryingReconnect=true;
try{AO.status=XMLHttpRequest.status
}catch(Ai){AO.status=500
}if(!AO.status){AO.status=500
}if(!AO.errorHandled){K();
Ag()
}}
}Ae.onreadystatechange=function(){if(AL){return 
}AO.error=null;
var Aj=false;
var Ap=false;
if(Ad.transport==="streaming"&&Ad.readyState>2&&Ae.readyState===4){K();
Ag();
return 
}Ad.readyState=Ae.readyState;
if(Ad.transport==="streaming"&&Ae.readyState>=3){Ap=true
}else{if(Ad.transport==="long-polling"&&Ae.readyState===4){Ap=true
}}J(P);
if(Ad.transport!=="polling"){var Ai=200;
if(Ae.readyState===4){Ai=Ae.status>1000?0:Ae.status
}if(!Ad.reconnectOnServerError&&(Ai>=300&&Ai<600)){q(Ai,Ae.statusText);
return 
}if(Ai>=300||Ai===0){Ac();
return 
}if((!Ad.enableProtocol||!Af.firstMessage)&&Ae.readyState===2){if(A.util.browser.mozilla&&AO.ffTryingReconnect){AO.ffTryingReconnect=false;
setTimeout(function(){if(!AO.ffTryingReconnect){v(Ad)
}},500)
}else{v(Ad)
}}}else{if(Ae.readyState===4){Ap=true
}}if(Ap){var Am=Ae.responseText;
AO.errorHandled=false;
if(A.util.trim(Am).length===0&&Ad.transport==="long-polling"){if(!Ae.hasData){AJ(Ae,Ad,Ad.pollingInterval)
}else{Ae.hasData=false
}return 
}Ae.hasData=true;
e(Ae,P);
if(Ad.transport==="streaming"){if(!A.util.browser.opera){var Al=Am.substring(Ad.lastIndex,Am.length);
Aj=R(Al,Ad,AO);
Ad.lastIndex=Am.length;
if(Aj){return 
}}else{A.util.iterate(function(){if(AO.status!==500&&Ae.responseText.length>Ad.lastIndex){try{AO.status=Ae.status;
AO.headers=A.util.parseHeaders(Ae.getAllResponseHeaders());
e(Ae,P)
}catch(Ar){AO.status=404
}J(P);
AO.state="messageReceived";
var Aq=Ae.responseText.substring(Ad.lastIndex);
Ad.lastIndex=Ae.responseText.length;
Aj=R(Aq,Ad,AO);
if(!Aj){AE()
}if(g(Ae,Ad)){h(Ae,Ad);
return 
}}else{if(AO.status>400){Ad.lastIndex=Ae.responseText.length;
return false
}}},0)
}}else{Aj=R(Am,Ad,AO)
}var Ao=g(Ae,Ad);
try{AO.status=Ae.status;
AO.headers=A.util.parseHeaders(Ae.getAllResponseHeaders());
e(Ae,Ad)
}catch(An){AO.status=404
}if(Ad.suspend){AO.state=AO.status===0?"closed":"messageReceived"
}else{AO.state="messagePublished"
}var Ak=!Ao&&Af.transport!=="streaming"&&Af.transport!=="polling";
if(Ak&&!Ad.executeCallbackBeforeReconnect){AJ(Ae,Ad,Ad.pollingInterval)
}if(AO.responseBody.length!==0&&!Aj){AE()
}if(Ak&&Ad.executeCallbackBeforeReconnect){AJ(Ae,Ad,Ad.pollingInterval)
}if(Ao){h(Ae,Ad)
}}};
try{Ae.send(Ad.data);
U=true
}catch(Ah){A.util.log(Ad.logLevel,["Unable to connect to "+Ad.url]);
q(0,Ah)
}}else{if(Ad.logLevel==="debug"){A.util.log(Ad.logLevel,["Max re-connection reached."])
}q(0,"maxRequest reached")
}}function h(Ad,Ac){f();
AL=false;
AJ(Ad,Ac,500)
}function j(Ae,Af,Ad){var Ac=Af.url;
if(Af.dispatchUrl!=null&&Af.method==="POST"){Ac+=Af.dispatchUrl
}Ac=O(Af,Ac);
Ac=A.util.prepareURL(Ac);
if(Ad){Ae.open(Af.method,Ac,Af.async);
if(Af.connectTimeout>0){Af.id=setTimeout(function(){if(Af.requestCount===0){K();
L("Connect timeout","closed",200,Af.transport)
}},Af.connectTimeout)
}}if(P.withCredentials&&P.transport!=="websocket"){if("withCredentials" in Ae){Ae.withCredentials=true
}}if(!P.dropHeaders){Ae.setRequestHeader("X-Atmosphere-Framework",A.util.version);
Ae.setRequestHeader("X-Atmosphere-Transport",Af.transport);
if(Ae.heartbeat!==null&&Ae.heartbeat.server!==null){Ae.setRequestHeader("X-Heartbeat-Server",Ae.heartbeat.server)
}if(Af.trackMessageLength){Ae.setRequestHeader("X-Atmosphere-TrackMessageSize","true")
}Ae.setRequestHeader("X-Atmosphere-tracking-id",Af.uuid);
A.util.each(Af.headers,function(Ag,Ai){var Ah=A.util.isFunction(Ai)?Ai.call(this,Ae,Af,Ad,AO):Ai;
if(Ah!=null){Ae.setRequestHeader(Ag,Ah)
}})
}if(Af.contentType!==""){Ae.setRequestHeader("Content-Type",Af.contentType)
}}function AJ(Ad,Ae,Af){if(Ae.reconnect||(Ae.suspend&&U)){var Ac=0;
if(Ad&&Ad.readyState>1){Ac=Ad.status>1000?0:Ad.status
}AO.status=Ac===0?204:Ac;
AO.reason=Ac===0?"Server resumed the connection or down.":"OK";
clearTimeout(Ae.id);
if(Ae.reconnectId){clearTimeout(Ae.reconnectId);
delete Ae.reconnectId
}if(Af>0){P.reconnectId=setTimeout(function(){i(Ae)
},Af)
}else{i(Ae)
}}}function Q(Ac){Ac.state="re-connecting";
AH(Ac)
}function AM(Ac){Ac.state="openAfterResume";
AH(Ac);
Ac.state="messageReceived"
}function m(Ac){if(Ac.transport!=="polling"){N=y(Ac);
N.open()
}else{y(Ac).open()
}}function y(Ae){var Ad=P;
if((Ae!=null)&&(typeof (Ae)!=="undefined")){Ad=Ae
}var Aj=Ad.transport;
var Ai=0;
var Ac=new window.XDomainRequest();
var Ag=function(){if(Ad.transport==="long-polling"&&(Ad.reconnect&&(Ad.maxRequest===-1||Ad.requestCount++<Ad.maxRequest))){Ac.status=200;
m(Ad)
}};
var Ah=Ad.rewriteURL||function(Al){var Ak=/(?:^|;\s*)(JSESSIONID|PHPSESSID)=([^;]*)/.exec(document.cookie);
switch(Ak&&Ak[1]){case"JSESSIONID":return Al.replace(/;jsessionid=[^\?]*|(\?)|$/,";jsessionid="+Ak[2]+"$1");
case"PHPSESSID":return Al.replace(/\?PHPSESSID=[^&]*&?|\?|$/,"?PHPSESSID="+Ak[2]+"&").replace(/&$/,"")
}return Al
};
Ac.onprogress=function(){Af(Ac)
};
Ac.onerror=function(){if(Ad.transport!=="polling"){K();
if(AU++<Ad.maxReconnectOnClose){if(Ad.reconnectInterval>0){Ad.reconnectId=setTimeout(function(){s("re-connecting",Ae.transport,Ae);
m(Ad)
},Ad.reconnectInterval)
}else{s("re-connecting",Ae.transport,Ae);
m(Ad)
}}else{q(0,"maxReconnectOnClose reached")
}}};
Ac.onload=function(){if(P.timedOut){P.timedOut=false;
K();
Ad.lastIndex=0;
if(Ad.reconnect&&AU++<Ad.maxReconnectOnClose){s("re-connecting",Ae.transport,Ae);
Ag()
}else{q(0,"maxReconnectOnClose reached")
}}};
var Af=function(Ak){clearTimeout(Ad.id);
var Am=Ak.responseText;
Am=Am.substring(Ai);
Ai+=Am.length;
if(Aj!=="polling"){J(Ad);
var Al=R(Am,Ad,AO);
if(Aj==="long-polling"&&A.util.trim(Am).length===0){return 
}if(Ad.executeCallbackBeforeReconnect){Ag()
}if(!Al){L(AO.responseBody,"messageReceived",200,Aj)
}if(!Ad.executeCallbackBeforeReconnect){Ag()
}}};
return{open:function(){var Ak=Ad.url;
if(Ad.dispatchUrl!=null){Ak+=Ad.dispatchUrl
}Ak=O(Ad,Ak);
Ac.open(Ad.method,Ah(Ak));
if(Ad.method==="GET"){Ac.send()
}else{Ac.send(Ad.data)
}if(Ad.connectTimeout>0){Ad.id=setTimeout(function(){if(Ad.requestCount===0){K();
L("Connect timeout","closed",200,Ad.transport)
}},Ad.connectTimeout)
}},close:function(){Ac.abort()
}}
}function AW(Ac){N=x(Ac);
N.open()
}function x(Af){var Ae=P;
if((Af!=null)&&(typeof (Af)!=="undefined")){Ae=Af
}var Ad;
var Ag=new window.ActiveXObject("htmlfile");
Ag.open();
Ag.close();
var Ac=Ae.url;
if(Ae.dispatchUrl!=null){Ac+=Ae.dispatchUrl
}if(Ae.transport!=="polling"){AO.transport=Ae.transport
}return{open:function(){var Ah=Ag.createElement("iframe");
Ac=O(Ae);
if(Ae.data!==""){Ac+="&X-Atmosphere-Post-Body="+encodeURIComponent(Ae.data)
}Ac=A.util.prepareURL(Ac);
Ah.src=Ac;
Ag.body.appendChild(Ah);
var Ai=Ah.contentDocument||Ah.contentWindow.document;
Ad=A.util.iterate(function(){try{if(!Ai.firstChild){return 
}var Al=Ai.body?Ai.body.lastChild:Ai;
var An=function(){var Ap=Al.cloneNode(true);
Ap.appendChild(Ai.createTextNode("."));
var Ao=Ap.innerText;
Ao=Ao.substring(0,Ao.length-1);
return Ao
};
if(!Ai.body||!Ai.body.firstChild||Ai.body.firstChild.nodeName.toLowerCase()!=="pre"){var Ak=Ai.head||Ai.getElementsByTagName("head")[0]||Ai.documentElement||Ai;
var Aj=Ai.createElement("script");
Aj.text="document.write('<plaintext>')";
Ak.insertBefore(Aj,Ak.firstChild);
Ak.removeChild(Aj);
Al=Ai.body.lastChild
}if(Ae.closed){Ae.isReopen=true
}Ad=A.util.iterate(function(){var Ap=An();
if(Ap.length>Ae.lastIndex){J(P);
AO.status=200;
AO.error=null;
Al.innerText="";
var Ao=R(Ap,Ae,AO);
if(Ao){return""
}L(AO.responseBody,"messageReceived",200,Ae.transport)
}Ae.lastIndex=0;
if(Ai.readyState==="complete"){AA(true);
s("re-connecting",Ae.transport,Ae);
if(Ae.reconnectInterval>0){Ae.reconnectId=setTimeout(function(){AW(Ae)
},Ae.reconnectInterval)
}else{AW(Ae)
}return false
}},null);
return false
}catch(Am){AO.error=true;
s("re-connecting",Ae.transport,Ae);
if(AU++<Ae.maxReconnectOnClose){if(Ae.reconnectInterval>0){Ae.reconnectId=setTimeout(function(){AW(Ae)
},Ae.reconnectInterval)
}else{AW(Ae)
}}else{q(0,"maxReconnectOnClose reached")
}Ag.execCommand("Stop");
Ag.close();
return false
}})
},close:function(){if(Ad){Ad()
}Ag.execCommand("Stop");
AA(true)
}}
}function T(Ac){if(AT!=null){c(Ac)
}else{if(Y!=null||AC!=null){l(Ac)
}else{if(N!=null){I(Ac)
}else{if(u!=null){Z(Ac)
}else{if(AS!=null){t(Ac)
}else{q(0,"No suspended connection available");
A.util.error("No suspended connection available. Make sure atmosphere.subscribe has been called and request.onOpen invoked before invoking this method")
}}}}}}function AI(Ad,Ac){if(!Ac){Ac=W(Ad)
}Ac.transport="polling";
Ac.method="GET";
Ac.withCredentials=false;
Ac.reconnect=false;
Ac.force=true;
Ac.suspend=false;
Ac.timeout=1000;
i(Ac)
}function c(Ac){AT.send(Ac)
}function AN(Ad){if(Ad.length===0){return 
}try{if(AT){AT.localSend(Ad)
}else{if(H){H.signal("localMessage",A.util.stringifyJSON({id:o,event:Ad}))
}}}catch(Ac){A.util.error(Ac)
}}function l(Ad){var Ac=W(Ad);
i(Ac)
}function I(Ad){if(P.enableXDR&&A.util.checkCORSSupport()){var Ac=W(Ad);
Ac.reconnect=false;
AX(Ac)
}else{l(Ad)
}}function Z(Ac){l(Ac)
}function k(Ac){var Ad=Ac;
if(typeof (Ad)==="object"){Ad=Ac.data
}return Ad
}function W(Ad){var Ae=k(Ad);
var Ac={connected:false,timeout:60000,method:"POST",url:P.url,contentType:P.contentType,headers:P.headers,reconnect:true,callback:null,data:Ae,suspend:false,maxRequest:-1,logLevel:"info",requestCount:0,withCredentials:P.withCredentials,async:P.async,transport:"polling",isOpen:true,attachHeadersAsQueryString:true,enableXDR:P.enableXDR,uuid:P.uuid,dispatchUrl:P.dispatchUrl,enableProtocol:false,messageDelimiter:"|",trackMessageLength:P.trackMessageLength,maxReconnectOnClose:P.maxReconnectOnClose,heartbeatTimer:P.heartbeatTimer,heartbeat:P.heartbeat};
if(typeof (Ad)==="object"){Ac=A.util.extend(Ac,Ad)
}return Ac
}function t(Ac){var Af=A.util.isBinary(Ac)?Ac:k(Ac);
var Ad;
try{if(P.dispatchUrl!=null){Ad=P.webSocketPathDelimiter+P.dispatchUrl+P.webSocketPathDelimiter+Af
}else{Ad=Af
}if(!AS.canSendMessage){A.util.error("WebSocket not connected.");
return 
}AS.send(Ad)
}catch(Ae){AS.onclose=function(Ag){};
K();
AV("Websocket failed. Downgrading to Comet and resending "+Ac);
l(Ac)
}}function d(Ad){var Ac=A.util.parseJSON(Ad);
if(Ac.id!==o){if(typeof (P.onLocalMessage)!=="undefined"){P.onLocalMessage(Ac.event)
}else{if(typeof (A.util.onLocalMessage)!=="undefined"){A.util.onLocalMessage(Ac.event)
}}}}function L(Af,Ac,Ad,Ae){AO.responseBody=Af;
AO.transport=Ae;
AO.status=Ad;
AO.state=Ac;
AE()
}function e(Ac,Ae){if(!Ae.readResponsesHeaders){if(!Ae.enableProtocol){Ae.uuid=o
}}else{try{var Ad=Ac.getResponseHeader("X-Atmosphere-tracking-id");
if(Ad&&Ad!=null){Ae.uuid=Ad.split(" ").pop()
}}catch(Af){}}}function AH(Ac){M(Ac,P);
M(Ac,A.util)
}function M(Ad,Ae){switch(Ad.state){case"messageReceived":AU=0;
if(typeof (Ae.onMessage)!=="undefined"){Ae.onMessage(Ad)
}if(typeof (Ae.onmessage)!=="undefined"){Ae.onmessage(Ad)
}break;
case"error":if(typeof (Ae.onError)!=="undefined"){Ae.onError(Ad)
}if(typeof (Ae.onerror)!=="undefined"){Ae.onerror(Ad)
}break;
case"opening":delete P.closed;
if(typeof (Ae.onOpen)!=="undefined"){Ae.onOpen(Ad)
}if(typeof (Ae.onopen)!=="undefined"){Ae.onopen(Ad)
}break;
case"messagePublished":if(typeof (Ae.onMessagePublished)!=="undefined"){Ae.onMessagePublished(Ad)
}break;
case"re-connecting":if(typeof (Ae.onReconnect)!=="undefined"){Ae.onReconnect(P,Ad)
}break;
case"closedByClient":if(typeof (Ae.onClientTimeout)!=="undefined"){Ae.onClientTimeout(P)
}break;
case"re-opening":delete P.closed;
if(typeof (Ae.onReopen)!=="undefined"){Ae.onReopen(P,Ad)
}break;
case"fail-to-reconnect":if(typeof (Ae.onFailureToReconnect)!=="undefined"){Ae.onFailureToReconnect(P,Ad)
}break;
case"unsubscribe":case"closed":var Ac=typeof (P.closed)!=="undefined"?P.closed:false;
if(!Ac){if(typeof (Ae.onClose)!=="undefined"){Ae.onClose(Ad)
}if(typeof (Ae.onclose)!=="undefined"){Ae.onclose(Ad)
}}P.closed=true;
break;
case"openAfterResume":if(typeof (Ae.onOpenAfterResume)!=="undefined"){Ae.onOpenAfterResume(P)
}break
}}function AA(Ac){if(AO.state!=="closed"){AO.state="closed";
AO.responseBody="";
AO.messages=[];
AO.status=!Ac?501:200;
AE()
}}function AE(){var Ae=function(Ah,Ai){Ai(AO)
};
if(AT==null&&n!=null){n(AO.responseBody)
}P.reconnect=P.mrequest;
var Ac=typeof (AO.responseBody)==="string";
var Af=(Ac&&P.trackMessageLength)?(AO.messages.length>0?AO.messages:[""]):new Array(AO.responseBody);
for(var Ad=0;
Ad<Af.length;
Ad++){if(Af.length>1&&Af[Ad].length===0){continue
}AO.responseBody=(Ac)?A.util.trim(Af[Ad]):Af[Ad];
if(AT==null&&n!=null){n(AO.responseBody)
}if((AO.responseBody.length===0||(Ac&&AG===AO.responseBody))&&AO.state==="messageReceived"){continue
}AH(AO);
if(F.length>0){if(P.logLevel==="debug"){A.util.debug("Invoking "+F.length+" global callbacks: "+AO.state)
}try{A.util.each(F,Ae)
}catch(Ag){A.util.log(P.logLevel,["Callback exception"+Ag])
}}if(typeof (P.callback)==="function"){if(P.logLevel==="debug"){A.util.debug("Invoking request callbacks")
}try{P.callback(AO)
}catch(Ag){A.util.log(P.logLevel,["Callback exception"+Ag])
}}}}this.subscribe=function(Ac){AK(Ac);
w()
};
this.execute=function(){w()
};
this.close=function(){f()
};
this.disconnect=function(){b()
};
this.getUrl=function(){return P.url
};
this.push=function(Ae,Ad){if(Ad!=null){var Ac=P.dispatchUrl;
P.dispatchUrl=Ad;
T(Ae);
P.dispatchUrl=Ac
}else{T(Ae)
}};
this.getUUID=function(){return P.uuid
};
this.pushLocal=function(Ac){AN(Ac)
};
this.enableProtocol=function(Ac){return P.enableProtocol
};
this.request=P;
this.response=AO
}};
A.subscribe=function(H,K,J){if(typeof (K)==="function"){A.addCallback(K)
}if(typeof (H)!=="string"){J=H
}else{J.url=H
}E=((typeof (J)!=="undefined")&&typeof (J.uuid)!=="undefined")?J.uuid:0;
var I=new A.AtmosphereRequest(J);
I.execute();
G[G.length]=I;
return I
};
A.unsubscribe=function(){if(G.length>0){var H=[].concat(G);
for(var J=0;
J<H.length;
J++){var I=H[J];
I.close();
clearTimeout(I.response.request.id);
if(I.heartbeatTimer){clearTimeout(I.heartbeatTimer)
}}}G=[];
F=[]
};
A.unsubscribeUrl=function(I){var H=-1;
if(G.length>0){for(var K=0;
K<G.length;
K++){var J=G[K];
if(J.getUrl()===I){J.close();
clearTimeout(J.response.request.id);
if(J.heartbeatTimer){clearTimeout(J.heartbeatTimer)
}H=K;
break
}}}if(H>=0){G.splice(H,1)
}};
A.addCallback=function(H){if(A.util.inArray(H,F)===-1){F.push(H)
}};
A.removeCallback=function(I){var H=A.util.inArray(I,F);
if(H!==-1){F.splice(H,1)
}};
A.util={browser:{},parseHeaders:function(I){var H,K=/^(.*?):[ \t]*([^\r\n]*)\r?$/mg,J={};
while(H=K.exec(I)){J[H[1]]=H[2]
}return J
},now:function(){return new Date().getTime()
},isArray:function(H){return Object.prototype.toString.call(H)==="[object Array]"
},inArray:function(J,K){if(!Array.prototype.indexOf){var H=K.length;
for(var I=0;
I<H;
++I){if(K[I]===J){return I
}}return -1
}return K.indexOf(J)
},isBinary:function(H){return/^\[object\s(?:Blob|ArrayBuffer|.+Array)\]$/.test(Object.prototype.toString.call(H))
},isFunction:function(H){return Object.prototype.toString.call(H)==="[object Function]"
},getAbsoluteURL:function(H){var I=document.createElement("div");
I.innerHTML='<a href="'+H+'"/>';
return encodeURI(decodeURI(I.firstChild.href))
},prepareURL:function(I){var J=A.util.now();
var H=I.replace(/([?&])_=[^&]*/,"$1_="+J);
return H+(H===I?(/\?/.test(I)?"&":"?")+"_="+J:"")
},trim:function(H){if(!String.prototype.trim){return H.toString().replace(/(?:(?:^|\n)\s+|\s+(?:$|\n))/g,"").replace(/\s+/g," ")
}else{return H.toString().trim()
}},param:function(L){var J,H=[];
function K(M,N){N=A.util.isFunction(N)?N():(N==null?"":N);
H.push(encodeURIComponent(M)+"="+encodeURIComponent(N))
}function I(N,O){var M;
if(A.util.isArray(O)){A.util.each(O,function(Q,P){if(/\[\]$/.test(N)){K(N,P)
}else{I(N+"["+(typeof P==="object"?Q:"")+"]",P)
}})
}else{if(Object.prototype.toString.call(O)==="[object Object]"){for(M in O){I(N+"["+M+"]",O[M])
}}else{K(N,O)
}}}for(J in L){I(J,L[J])
}return H.join("&").replace(/%20/g,"+")
},storage:function(){try{return !!(window.localStorage&&window.StorageEvent)
}catch(H){return false
}},iterate:function(J,I){var K;
I=I||0;
(function H(){K=setTimeout(function(){if(J()===false){return 
}H()
},I)
})();
return function(){clearTimeout(K)
}
},each:function(M,N,I){if(!M){return 
}var L,J=0,K=M.length,H=A.util.isArray(M);
if(I){if(H){for(;
J<K;
J++){L=N.apply(M[J],I);
if(L===false){break
}}}else{for(J in M){L=N.apply(M[J],I);
if(L===false){break
}}}}else{if(H){for(;
J<K;
J++){L=N.call(M[J],J,M[J]);
if(L===false){break
}}}else{for(J in M){L=N.call(M[J],J,M[J]);
if(L===false){break
}}}}return M
},extend:function(K){var J,I,H;
for(J=1;
J<arguments.length;
J++){if((I=arguments[J])!=null){for(H in I){K[H]=I[H]
}}}return K
},on:function(J,I,H){if(J.addEventListener){J.addEventListener(I,H,false)
}else{if(J.attachEvent){J.attachEvent("on"+I,H)
}}},off:function(J,I,H){if(J.removeEventListener){J.removeEventListener(I,H,false)
}else{if(J.detachEvent){J.detachEvent("on"+I,H)
}}},log:function(J,I){if(window.console){var H=window.console[J];
if(typeof H==="function"){H.apply(window.console,I)
}}},warn:function(){A.util.log("warn",arguments)
},info:function(){A.util.log("info",arguments)
},debug:function(){A.util.log("debug",arguments)
},error:function(){A.util.log("error",arguments)
},xhr:function(){try{return new window.XMLHttpRequest()
}catch(I){try{return new window.ActiveXObject("Microsoft.XMLHTTP")
}catch(H){}}},parseJSON:function(H){return !H?null:window.JSON&&window.JSON.parse?window.JSON.parse(H):new Function("return "+H)()
},stringifyJSON:function(J){var M=/[\\\"\x00-\x1f\x7f-\x9f\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g,K={"\b":"\\b","\t":"\\t","\n":"\\n","\f":"\\f","\r":"\\r",'"':'\\"',"\\":"\\\\"};
function H(N){return'"'+N.replace(M,function(O){var P=K[O];
return typeof P==="string"?P:"\\u"+("0000"+O.charCodeAt(0).toString(16)).slice(-4)
})+'"'
}function I(N){return N<10?"0"+N:N
}return window.JSON&&window.JSON.stringify?window.JSON.stringify(J):(function L(S,R){var Q,P,N,O,U=R[S],T=typeof U;
if(U&&typeof U==="object"&&typeof U.toJSON==="function"){U=U.toJSON(S);
T=typeof U
}switch(T){case"string":return H(U);
case"number":return isFinite(U)?String(U):"null";
case"boolean":return String(U);
case"object":if(!U){return"null"
}switch(Object.prototype.toString.call(U)){case"[object Date]":return isFinite(U.valueOf())?'"'+U.getUTCFullYear()+"-"+I(U.getUTCMonth()+1)+"-"+I(U.getUTCDate())+"T"+I(U.getUTCHours())+":"+I(U.getUTCMinutes())+":"+I(U.getUTCSeconds())+'Z"':"null";
case"[object Array]":N=U.length;
O=[];
for(Q=0;
Q<N;
Q++){O.push(L(Q,U)||"null")
}return"["+O.join(",")+"]";
default:O=[];
for(Q in U){if(B.call(U,Q)){P=L(Q,U);
if(P){O.push(H(Q)+":"+P)
}}}return"{"+O.join(",")+"}"
}}})("",{"":J})
},checkCORSSupport:function(){if(A.util.browser.msie&&!window.XDomainRequest&&+A.util.browser.version.split(".")[0]<11){return true
}else{if(A.util.browser.opera&&+A.util.browser.version.split(".")<12){return true
}else{if(A.util.trim(navigator.userAgent).slice(0,16)==="KreaTVWebKit/531"){return true
}else{if(A.util.trim(navigator.userAgent).slice(-7).toLowerCase()==="kreatel"){return true
}}}}var H=navigator.userAgent.toLowerCase();
var I=H.indexOf("android")>-1;
if(I){return true
}return false
}};
D=A.util.now();
(function(){var I=navigator.userAgent.toLowerCase(),H=/(chrome)[ \/]([\w.]+)/.exec(I)||/(webkit)[ \/]([\w.]+)/.exec(I)||/(opera)(?:.*version|)[ \/]([\w.]+)/.exec(I)||/(msie) ([\w.]+)/.exec(I)||/(trident)(?:.*? rv:([\w.]+)|)/.exec(I)||I.indexOf("compatible")<0&&/(mozilla)(?:.*? rv:([\w.]+)|)/.exec(I)||[];
A.util.browser[H[1]||""]=true;
A.util.browser.version=H[2]||"0";
if(A.util.browser.trident){A.util.browser.msie=true
}if(A.util.browser.msie||(A.util.browser.mozilla&&+A.util.browser.version.split(".")[0]===1)){A.util.storage=false
}})();
A.util.on(window,"unload",function(H){A.unsubscribe()
});
A.util.on(window,"keypress",function(H){if(H.charCode===27||H.keyCode===27){if(H.preventDefault){H.preventDefault()
}}});
A.util.on(window,"offline",function(){if(G.length>0){var H=[].concat(G);
for(var J=0;
J<H.length;
J++){var I=H[J];
I.close();
clearTimeout(I.response.request.id);
if(I.heartbeatTimer){clearTimeout(I.heartbeatTimer)
}}}});
A.util.on(window,"online",function(){if(G.length>0){for(var H=0;
H<G.length;
H++){G[H].execute()
}}});
return A
}));;(function(E,D){D.ui=D.ui||{};
var C=function(H){H.stopPropagation();
H.preventDefault()
};
var A=function(H){if(typeof H.onselectstart!="undefined"){E(D.getDomElement(H)).bind("selectstart",C)
}else{E(D.getDomElement(H)).bind("mousedown",C)
}};
var G=function(H){if(typeof H.onselectstart!="undefined"){E(D.getDomElement(H)).unbind("selectstart",C)
}else{E(D.getDomElement(H)).unbind("mousedown",C)
}};
var B={width:-1,height:-1,minWidth:-1,minHeight:-1,modal:true,moveable:true,resizeable:false,autosized:false,left:"auto",top:"auto",zindex:100,shadowDepth:5,shadowOpacity:0.1,attachToBody:true};
D.ui.PopupPanel=function(I,H){F.constructor.call(this,I);
this.markerId=I;
this.attachToDom(this.markerId);
this.options=E.extend(this.options,B,H||{});
this.minWidth=this.getMinimumSize(this.options.minWidth);
this.minHeight=this.getMinimumSize(this.options.minHeight);
this.maxWidth=this.options.maxWidth;
this.maxHeight=this.options.maxHeight;
this.baseZIndex=this.options.zindex;
this.div=E(D.getDomElement(I));
this.cdiv=E(D.getDomElement(I+"_container"));
this.contentDiv=E(D.getDomElement(I+"_content"));
this.shadowDiv=E(D.getDomElement(I+"_shadow"));
this.shadeDiv=E(D.getDomElement(I+"_shade"));
this.scrollerDiv=E(D.getDomElement(I+"_content_scroller"));
E(this.shadowDiv).css("opacity",this.options.shadowOpacity);
this.shadowDepth=parseInt(this.options.shadowDepth);
this.borders=new Array();
this.firstHref=E(D.getDomElement(I+"FirstHref"));
if(this.options.resizeable){this.borders.push(new D.ui.PopupPanel.Border(I+"ResizerN",this,"N-resize",D.ui.PopupPanel.Sizer.N));
this.borders.push(new D.ui.PopupPanel.Border(I+"ResizerE",this,"E-resize",D.ui.PopupPanel.Sizer.E));
this.borders.push(new D.ui.PopupPanel.Border(I+"ResizerS",this,"S-resize",D.ui.PopupPanel.Sizer.S));
this.borders.push(new D.ui.PopupPanel.Border(I+"ResizerW",this,"W-resize",D.ui.PopupPanel.Sizer.W));
this.borders.push(new D.ui.PopupPanel.Border(I+"ResizerNW",this,"NW-resize",D.ui.PopupPanel.Sizer.NW));
this.borders.push(new D.ui.PopupPanel.Border(I+"ResizerNE",this,"NE-resize",D.ui.PopupPanel.Sizer.NE));
this.borders.push(new D.ui.PopupPanel.Border(I+"ResizerSE",this,"SE-resize",D.ui.PopupPanel.Sizer.SE));
this.borders.push(new D.ui.PopupPanel.Border(I+"ResizerSW",this,"SW-resize",D.ui.PopupPanel.Sizer.SW))
}if(this.options.moveable&&D.getDomElement(I+"_header")){this.header=new D.ui.PopupPanel.Border(I+"_header",this,"move",D.ui.PopupPanel.Sizer.Header)
}else{E(D.getDomElement(I+"_header")).css("cursor","default")
}this.resizeProxy=E.proxy(this.resizeListener,this);
this.cdiv.resize(this.resizeProxy);
this.findForm(this.cdiv).on("ajaxcomplete",this.resizeProxy)
};
D.BaseComponent.extend(D.ui.PopupPanel);
var F=D.ui.PopupPanel.$super;
E.extend(D.ui.PopupPanel.prototype,(function(H){return{name:"PopupPanel",saveInputValues:function(I){if(D.browser.msie){E("input[type=checkbox], input[type=radio]",I).each(function(J){E(this).defaultChecked=E(this).checked
})
}},width:function(){return this.getContentElement()[0].clientWidth
},height:function(){return this.getContentElement()[0].clientHeight
},getLeft:function(){return this.cdiv.css("left")
},getTop:function(){return this.cdiv.css("top")
},getInitialSize:function(){if(this.options.autosized){return 15
}else{return E(D.getDomElement(this.markerId+"_header_content")).height()
}},getContentElement:function(){if(!this._contentElement){this._contentElement=this.cdiv
}return this._contentElement
},getSizeElement:function(){return document.body
},getMinimumSize:function(I){return Math.max(I,2*this.getInitialSize()+2)
},__getParsedOption:function(J,I){var K=parseInt(J[I],10);
if(K<0||isNaN(K)){K=this[I]
}return K
},destroy:function(){this.findForm(this.cdiv).off("ajaxcomplete",this.resizeProxy);
this._contentElement=null;
this.firstOutside=null;
this.lastOutside=null;
this.firstHref=null;
this.parent=null;
if(this.header){this.header.destroy();
this.header=null
}for(var I=0;
I<this.borders.length;
I++){this.borders[I].destroy()
}this.borders=null;
if(this.domReattached){this.div.remove()
}this.markerId=null;
this.options=null;
this.div=null;
this.cdiv=null;
this.contentDiv=null;
this.shadowDiv=null;
this.scrollerDiv=null;
this.userOptions=null;
this.eIframe=null;
F.destroy.call(this)
},initIframe:function(){if(this.contentWindow){E(this.contentWindow.document.body).css("margin","0px 0px 0px 0px")
}else{}if("transparent"==E(document.body).css("background-color")){E(this).css("filter","alpha(opacity=0)");
E(this).css("opacity","0")
}},setLeft:function(I){if(!isNaN(I)){this.cdiv.css("left",I+"px")
}},setTop:function(I){if(!isNaN(I)){this.cdiv.css("top",I+"px")
}},show:function(Z,T){var J=this.cdiv;
if(!this.shown&&this.invokeEvent("beforeshow",Z,null,J)){this.preventFocus();
if(!this.domReattached){this.parent=this.div.parent();
var V;
if(T){V=T.domElementAttachment
}if(!V){V=this.options.domElementAttachment
}var S;
if("parent"==V){S=this.parent
}else{if("form"==V){S=this.findForm(J)[0]||document.body
}else{S=document.body
}}if(S!=this.parent){this.saveInputValues(J);
this.shadeDiv.length&&S.appendChild(this.shadeDiv.get(0));
S.appendChild(this.cdiv.get(0));
this.domReattached=true
}else{this.parent.show()
}}var O=E("form",J);
if(this.options.keepVisualState&&O){for(var a=0;
a<O.length;
a++){var I=this;
E(O[a]).bind("submit",{popup:I},this.setStateInput)
}}var N={};
this.userOptions={};
E.extend(N,this.options);
if(T){E.extend(N,T);
E.extend(this.userOptions,T)
}if(this.options.autosized){if(N.left){var b;
if(N.left!="auto"){b=parseInt(N.left,10)
}else{var L=this.__calculateWindowWidth();
var P=this.width();
if(L>=P){b=(L-P)/2
}else{b=0
}}this.setLeft(Math.round(b));
E(this.shadowDiv).css("left",this.shadowDepth)
}if(N.top){var X;
if(N.top!="auto"){X=parseInt(N.top,10)
}else{var R=this.__calculateWindowHeight();
var c=this.height();
if(R>=c){X=(R-c)/2
}else{X=0
}}this.setTop(Math.round(X));
E(this.shadowDiv).css("top",this.shadowDepth);
E(this.shadowDiv).css("bottom",-this.shadowDepth)
}this.doResizeOrMove(D.ui.PopupPanel.Sizer.Diff.EMPTY)
}this.currentMinHeight=this.getMinimumSize(this.__getParsedOption(N,"minHeight"));
this.currentMinWidth=this.getMinimumSize(this.__getParsedOption(N,"minWidth"));
var K=this.getContentElement();
if(!this.options.autosized){if(N.width&&N.width==-1){N.width=300
}if(N.height&&N.height==-1){N.height=200
}}this.div.css("visibility","");
if(D.browser.msie){E(this.cdiv).find("input").each(function(){var d=E(this);
if(d.parents(".rf-pp-cntr").first().attr("id")===J.attr("id")){d.css("visibility",d.css("visibility"))
}})
}this.div.css("display","block");
if(this.options.autosized){this.shadowDiv.css("width",this.cdiv[0].clientWidth)
}if(N.width&&N.width!=-1||N.autosized){var W;
if(N.autosized){W=this.getStyle(this.getContentElement(),"width");
if(this.currentMinWidth>W){W=this.currentMinWidth
}if(W>this.maxWidth){W=this.maxWidth
}}else{if(this.currentMinWidth>N.width){N.width=this.currentMinWidth
}if(N.width>this.maxWidth){N.width=this.maxWidth
}W=N.width
}E(D.getDomElement(K)).css("width",W+(/px/.test(W)?"":"px"));
this.shadowDiv.css("width",W+(/px/.test(W)?"":"px"));
this.scrollerDiv.css("width",W+(/px/.test(W)?"":"px"))
}if(N.height&&N.height!=-1||N.autosized){var U;
if(N.autosized){U=this.getStyle(this.getContentElement(),"height");
if(this.currentMinHeight>U){U=this.currentMinHeight
}if(U>this.maxHeight){U=this.maxHeight
}}else{if(this.currentMinHeight>N.height){N.height=this.currentMinHeight
}if(N.height>this.maxHeight){N.height=this.maxHeight
}U=N.height
}E(D.getDomElement(K)).css("height",U+(/px/.test(U)?"":"px"));
var Y=E(D.getDomElement(this.markerId+"_header"))?E(D.getDomElement(this.markerId+"_header")).innerHeight():0;
this.shadowDiv.css("height",U+(/px/.test(U)?"":"px"));
this.scrollerDiv.css("height",U-Y+(/px/.test(U)?"":"px"))
}var Q;
if(this.options.overlapEmbedObjects&&!this.iframe){this.iframe=this.markerId+"IFrame";
E('<iframe src="javascript:\'\'" frameborder="0" scrolling="no" id="'+this.iframe+'" class="rf-pp-ifr" style="width:'+this.options.width+"px; height:"+this.options.height+'px;"></iframe>').insertBefore(E(":first-child",this.cdiv)[0]);
Q=E(D.getDomElement(this.iframe));
Q.bind("load",this.initIframe);
this.eIframe=Q
}if(N.left){var b;
if(N.left!="auto"){b=parseInt(N.left,10)
}else{var L=this.__calculateWindowWidth();
var P=this.width();
if(L>=P){b=(L-P)/2
}else{b=0
}}this.setLeft(Math.round(b));
E(this.shadowDiv).css("left",this.shadowDepth)
}if(N.top){var X;
if(N.top!="auto"){X=parseInt(N.top,10)
}else{var R=this.__calculateWindowHeight();
var c=this.height();
if(R>=c){X=(R-c)/2
}else{X=0
}}this.setTop(Math.round(X));
E(this.shadowDiv).css("top",this.shadowDepth);
E(this.shadowDiv).css("bottom",-this.shadowDepth)
}var M={};
M.parameters=T||{};
this.shown=true;
this.scrollerSizeDelta=parseInt(this.shadowDiv.css("height"))-parseInt(this.scrollerDiv.css("height"));
this.invokeEvent("show",M,null,J)
}},__calculateWindowHeight:function(){var I=document.documentElement;
return self.innerHeight||(I&&I.clientHeight)||document.body.clientHeight
},__calculateWindowWidth:function(){var I=document.documentElement;
return self.innerWidth||(I&&I.clientWidth)||document.body.clientWidth
},startDrag:function(I){A(document.body)
},firstOnfocus:function(I){var J=E(I.data.popup.firstHref);
if(J){J.focus()
}},processAllFocusElements:function(J,N){var I=-1;
var L;
var K="|a|input|select|button|textarea|";
if(J.focus&&J.nodeType==1&&(L=J.tagName)&&(I=K.indexOf(L.toLowerCase()))!=-1&&K.charAt(I-1)==="|"&&K.charAt(I+L.length)==="|"&&!J.disabled&&J.type!="hidden"){N.call(this,J)
}else{if(J!=this.cdiv.get(0)){var M=J.firstChild;
while(M){if(!M.style||M.style.display!="none"){this.processAllFocusElements(M,N)
}M=M.nextSibling
}}}},processTabindexes:function(I){if(!this.firstOutside){this.firstOutside=I
}if(!I.prevTabIndex){I.prevTabIndex=I.tabIndex;
I.tabIndex=-1
}if(!I.prevAccessKey){I.prevAccessKey=I.accessKey;
I.accessKey=""
}},restoreTabindexes:function(I){if(I.prevTabIndex!=undefined){if(I.prevTabIndex==0){E(I).removeAttr("tabindex")
}else{I.tabIndex=I.prevTabIndex
}I.prevTabIndex=undefined
}if(I.prevAccessKey!=undefined){if(I.prevAccessKey==""){E(I).removeAttr("accesskey")
}else{I.accessKey=I.prevAccessKey
}I.prevAccessKey=undefined
}},preventFocus:function(){if(this.options.modal){this.processAllFocusElements(document,this.processTabindexes);
var I=this;
if(this.firstOutside){E(D.getDomElement(this.firstOutside)).bind("focus",{popup:I},this.firstOnfocus)
}}},restoreFocus:function(){if(this.options.modal){this.processAllFocusElements(document,this.restoreTabindexes);
if(this.firstOutside){E(D.getDomElement(this.firstOutside)).unbind("focus",this.firstOnfocus);
this.firstOutside=null
}}},endDrag:function(J){for(var I=0;
I<this.borders.length;
I++){this.borders[I].show();
this.borders[I].doPosition()
}G(document.body)
},hide:function(M,L){var K=this.cdiv;
this.restoreFocus();
if(this.shown&&this.invokeEvent("beforehide",M,null,K)){this.currentMinHeight=undefined;
this.currentMinWidth=undefined;
this.div.hide();
if(this.parent){if(this.domReattached){this.saveInputValues(K);
var O=this.div.get(0);
this.shadeDiv.length&&O.appendChild(this.shadeDiv.get(0));
O.appendChild(K.get(0));
this.domReattached=false
}}var N={};
N.parameters=L||{};
var I=E("form",K);
if(this.options.keepVisualState&&I){for(var J=0;
J<I.length;
J++){E(I[J]).unbind("submit",this.setStateInput)
}}this.shown=false;
this.invokeEvent("hide",N,null,K);
this.setLeft(10);
this.setTop(10)
}},getStyle:function(J,I){return parseInt(E(D.getDomElement(J)).css(I).replace("px",""),10)
},resizeListener:function(I,J){this.doResizeOrMove(D.ui.PopupPanel.Sizer.Diff.EMPTY)
},doResizeOrMove:function(S){var N={};
var Z={};
var R={};
var M={};
var Q={};
var P={};
var T={};
var I;
var Y=this.scrollerSizeDelta;
var b=0;
var L=this.getContentElement();
var J=S===D.ui.PopupPanel.Sizer.Diff.EMPTY||S.deltaWidth||S.deltaHeight;
if(J){if(this.options.autosized){this.resetHeight();
this.resetWidth()
}I=this.getStyle(L,"width");
var V=I;
I+=S.deltaWidth||0;
if(I>=this.currentMinWidth){M.width=I+"px";
Q.width=I+"px";
P.width=I-b+"px";
T.width=I-b+"px"
}else{M.width=this.currentMinWidth+"px";
Q.width=this.currentMinWidth+"px";
P.width=this.currentMinWidth-b+"px";
T.width=this.currentMinWidth-b+"px";
if(S.deltaWidth){N.vx=V-this.currentMinWidth;
N.x=true
}}if(I>this.options.maxWidth){M.width=this.options.maxWidth+"px";
Q.width=this.options.maxWidth+"px";
P.width=this.options.maxWidth-b+"px";
T.width=this.options.maxWidth-b+"px";
if(S.deltaWidth){N.vx=V-this.options.maxWidth;
N.x=true
}}}if(N.vx&&S.deltaX){S.deltaX=-N.vx
}var X=E(this.cdiv);
if(S.deltaX&&(N.vx||!N.x)){if(N.vx){S.deltaX=N.vx
}var U=this.getStyle(X,"left");
U+=S.deltaX;
R.left=U+"px"
}if(J){I=this.getStyle(L,"height");
var a=I;
I+=S.deltaHeight||0;
if(I>=this.currentMinHeight){M.height=I+"px";
Q.height=I+"px";
T.height=I-Y+"px"
}else{M.height=this.currentMinHeight+"px";
Q.height=this.currentMinHeight+"px";
T.height=this.currentMinHeight-Y+"px";
if(S.deltaHeight){N.vy=a-this.currentMinHeight;
N.y=true
}}if(I>this.options.maxHeight){M.height=this.options.maxHeight+"px";
Q.height=this.options.maxHeight+"px";
T.height=this.options.maxHeight-Y+"px";
if(S.deltaHeight){N.vy=a-this.options.maxHeight;
N.y=true
}}}if(N.vy&&S.deltaY){S.deltaY=-N.vy
}if(S.deltaY&&(N.vy||!N.y)){if(N.vy){S.deltaY=N.vy
}var K=this.getStyle(X,"top");
K+=S.deltaY;
R.top=K+"px"
}L.css(M);
this.scrollerDiv.css(T);
if(this.eIframe){this.eIframe.css(T)
}this.shadowDiv.css(Q);
X.css(R);
this.shadowDiv.css(Z);
E.extend(this.userOptions,R);
E.extend(this.userOptions,M);
var O=this.width();
var W=this.height();
this.reductionData=null;
if(O<=2*this.getInitialSize()){this.reductionData={};
this.reductionData.w=O
}if(W<=2*this.getInitialSize()){if(!this.reductionData){this.reductionData={}
}this.reductionData.h=W
}if(this.header){this.header.doPosition()
}return N
},resetWidth:function(){this.getContentElement().css("width","");
this.scrollerDiv.css("width","");
if(this.eIframe){this.eIframe.css("width","")
}this.shadowDiv.css("width","");
E(this.cdiv).css("width","")
},resetHeight:function(){this.getContentElement().css("height","");
this.scrollerDiv.css("height","");
if(this.eIframe){this.eIframe.css("height","")
}this.shadowDiv.css("height","");
E(this.cdiv).css("height","")
},setSize:function(L,I){var J=L-this.width();
var K=I-this.height();
var M=new D.ui.PopupPanel.Sizer.Diff(0,0,J,K);
this.doResizeOrMove(M)
},moveTo:function(J,I){this.cdiv.css("top",J);
this.cdiv.css("left",I)
},move:function(J,I){var K=new D.ui.PopupPanel.Sizer.Diff(J,I,0,0);
this.doResizeOrMove(K)
},resize:function(J,I){var K=new D.ui.PopupPanel.Sizer.Diff(0,0,J,I);
this.doResizeOrMove(K)
},findForm:function(I){var J=I;
while(J){if(J[0]&&(!J[0].tagName||J[0].tagName.toLowerCase()!="form")){J=E(J).parent()
}else{break
}}return J
},setStateInput:function(K){var I=K.data.popup;
target=E(I.findForm(K.currentTarget));
var J=document.createElement("input");
J.type="hidden";
J.id=I.markerId+"OpenedState";
J.name=I.markerId+"OpenedState";
J.value=I.shown?"true":"false";
target.append(J);
E.each(I.userOptions,function(L,M){J=document.createElement("input");
J.type="hidden";
J.id=I.markerId+"StateOption_"+L;
J.name=I.markerId+"StateOption_"+L;
J.value=M;
target.append(J)
});
return true
}}
})());
E.extend(D.ui.PopupPanel,{showPopupPanel:function(J,I,H){D.Event.ready(function(){D.component(J).show()
})
},hidePopupPanel:function(J,I,H){D.Event.ready(function(){D.component(J).hide()
})
}})
})(RichFaces.jQuery,window.RichFaces);;(function(B,A){A.ui=A.ui||{};
A.ui.InputNumberSlider=A.BaseComponent.extendClass({name:"InputNumberSlider",delay:200,maxValue:100,minValue:0,step:1,tabIndex:0,decreaseSelectedClass:"rf-insl-dec-sel",handleSelectedClass:"rf-insl-hnd-sel",increaseSelectedClass:"rf-insl-inc-sel",init:function(H,D,C){$superInputNumberSlider.constructor.call(this,H);
B.extend(this,D);
this.range=this.maxValue-this.minValue;
this.id=H;
this.element=B(this.attachToDom());
this.input=this.element.children(".rf-insl-inp-cntr").children(".rf-insl-inp");
this.track=this.element.children(".rf-insl-trc-cntr").children(".rf-insl-trc");
this.handleContainer=this.track.children("span");
this.handle=this.handleContainer.children(".rf-insl-hnd, .rf-insl-hnd-dis");
this.tooltip=this.element.children(".rf-insl-tt");
var G=Number(this.input.val());
if(isNaN(G)){G=this.minValue
}this.handleContainer.css("display","block");
this.track.css("padding-right",this.handle.width()+"px");
this.__setValue(G,null,true);
if(!this.disabled){this.decreaseButton=this.element.children(".rf-insl-dec");
this.increaseButton=this.element.children(".rf-insl-inc");
this.track[0].tabIndex=this.tabIndex;
for(var F in C){this[F]+=" "+C[F]
}var E=B.proxy(this.__inputHandler,this);
this.input.change(E);
this.input.submit(E);
this.element.mousewheel(B.proxy(this.__mousewheelHandler,this));
this.track.keydown(B.proxy(this.__keydownHandler,this));
this.decreaseButton.mousedown(B.proxy(this.__decreaseHandler,this));
this.increaseButton.mousedown(B.proxy(this.__increaseHandler,this));
this.track.mousedown(B.proxy(this.__mousedownHandler,this))
}},decrease:function(C){var D=this.value-this.step;
D=this.roundFloat(D);
this.setValue(D,C)
},increase:function(C){var D=this.value+this.step;
D=this.roundFloat(D);
this.setValue(D,C)
},getValue:function(){return this.value
},setValue:function(D,C){if(!this.disabled){this.__setValue(D,C)
}},roundFloat:function(C){var F=this.step.toString();
var E=0;
if(!/\./.test(F)){if(this.step>=1){return C
}if(/e/.test(F)){E=F.split("-")[1]
}}else{E=F.length-F.indexOf(".")-1
}var D=C.toFixed(E);
return parseFloat(D)
},__setValue:function(D,C,F){if(!isNaN(D)){var G=false;
if(this.input.val()==""){G=true
}if(D>this.maxValue){D=this.maxValue;
this.input.val(D);
G=true
}else{if(D<this.minValue){D=this.minValue;
this.input.val(D);
G=true
}}if(D!=this.value||G){this.input.val(D);
var E=100*(D-this.minValue)/this.range;
if(this.handleType=="bar"){this.handleContainer.css("width",E+"%")
}else{this.handleContainer.css("padding-left",E+"%")
}this.tooltip.text(D);
this.tooltip.setPosition(this.handle,{from:"LT",offset:[0,5]});
this.value=D;
if(this.onchange&&!F){this.onchange.call(this.element[0],C)
}}}},__inputHandler:function(C){var D=Number(this.input.val());
if(isNaN(D)){this.input.val(this.value)
}else{this.__setValue(D,C)
}},__mousewheelHandler:function(E,F,D,C){F=D||C;
if(F>0){this.increase(E)
}else{if(F<0){this.decrease(E)
}}return false
},__keydownHandler:function(C){if(C.keyCode==37){var D=Number(this.input.val())-this.step;
D=this.roundFloat(D);
this.__setValue(D,C);
C.preventDefault()
}else{if(C.keyCode==39){var D=Number(this.input.val())+this.step;
D=this.roundFloat(D);
this.__setValue(D,C);
C.preventDefault()
}}},__decreaseHandler:function(D){var C=this;
C.decrease(D);
this.intervalId=window.setInterval(function(){C.decrease(D)
},this.delay);
B(document).one("mouseup",true,B.proxy(this.__clearInterval,this));
this.decreaseButton.addClass(this.decreaseSelectedClass);
D.preventDefault()
},__increaseHandler:function(D){var C=this;
C.increase(D);
this.intervalId=window.setInterval(function(){C.increase(D)
},this.delay);
B(document).one("mouseup",B.proxy(this.__clearInterval,this));
this.increaseButton.addClass(this.increaseSelectedClass);
D.preventDefault()
},__clearInterval:function(C){window.clearInterval(this.intervalId);
if(C.data){this.decreaseButton.removeClass(this.decreaseSelectedClass)
}else{this.increaseButton.removeClass(this.increaseSelectedClass)
}},__mousedownHandler:function(D){this.__mousemoveHandler(D);
this.track.focus();
var C=B(document);
C.mousemove(B.proxy(this.__mousemoveHandler,this));
C.one("mouseup",B.proxy(this.__mouseupHandler,this));
this.handle.addClass(this.handleSelectedClass);
this.tooltip.show()
},__mousemoveHandler:function(C){var D=this.range*(C.pageX-this.track.offset().left-this.handle.width()/2)/(this.track.width()-this.handle.width())+this.minValue;
D=Math.round(D/this.step)*this.step;
D=this.roundFloat(D);
this.__setValue(D,C);
C.preventDefault()
},__mouseupHandler:function(){this.handle.removeClass(this.handleSelectedClass);
this.tooltip.hide();
B(document).unbind("mousemove",this.__mousemoveHandler)
},destroy:function(C){B(document).unbind("mousemove",this.__mousemoveHandler);
$superInputNumberSlider.destroy.call(this)
}});
$superInputNumberSlider=A.ui.InputNumberSlider.$super
}(RichFaces.jQuery,window.RichFaces));;window.RichFaces=window.RichFaces||{};
RichFaces.jQuery=RichFaces.jQuery||window.jQuery;
(function(A){A.Selection=A.Selection||{};
A.Selection.set=function(D,E,B){if(D.setSelectionRange){D.focus();
D.setSelectionRange(E,B)
}else{if(D.createTextRange){var C=D.createTextRange();
C.collapse(true);
C.moveEnd("character",B);
C.moveStart("character",E);
C.select()
}}};
A.Selection.getStart=function(C){if(C.setSelectionRange){return C.selectionStart
}else{if(document.selection&&document.selection.createRange){var B=document.selection.createRange().duplicate();
B.moveEnd("character",C.value.length);
if(B.text==""){return C.value.length
}return C.value.lastIndexOf(B.text)
}}};
A.Selection.getEnd=function(C){if(C.setSelectionRange){return C.selectionEnd
}else{if(document.selection&&document.selection.createRange){var B=document.selection.createRange().duplicate();
B.moveStart("character",-C.value.length);
return B.text.length
}}};
A.Selection.setCaretTo=function(B,C){if(!C){C=B.value.length
}A.Selection.set(B,C,C)
}
})(RichFaces);;(function(B,A){A.ui=A.ui||{};
A.ui.Accordion=A.ui.TogglePanel.extendClass({name:"Accordion",init:function(E,D){C.constructor.call(this,E,D);
this.items=[];
this.isKeepHeight=D.isKeepHeight||false
},getHeight:function(D){if(D||!this.__height){this.__height=B(A.getDomElement(this.id)).outerHeight(true)
}return this.__height
},getInnerHeight:function(D){if(D||!this.__innerHeight){this.__innerHeight=B(A.getDomElement(this.id)).innerHeight()
}return this.__innerHeight
},destroy:function(){A.Event.unbindById(this.id,"."+this.namespace);
C.destroy.call(this)
}});
var C=A.ui.Accordion.$super
})(RichFaces.jQuery,RichFaces);;/*
 * jQuery UI Widget 1.11.2
 * http://jqueryui.com
 *
 * Copyright 2014 jQuery Foundation and other contributors
 * Released under the MIT license.
 * http://jquery.org/license
 *
 * http://api.jqueryui.com/jQuery.widget/
 */
(function(A){if(typeof define==="function"&&define.amd){define(["jquery"],A)
}else{A(jQuery)
}}(function(B){var C=0,A=Array.prototype.slice;
B.cleanData=(function(D){return function(E){var G,H,F;
for(F=0;
(H=E[F])!=null;
F++){try{G=B._data(H,"events");
if(G&&G.remove){B(H).triggerHandler("remove")
}}catch(I){}}D(E)
}
})(B.cleanData);
B.widget=function(D,E,L){var I,J,G,K,F={},H=D.split(".")[0];
D=D.split(".")[1];
I=H+"-"+D;
if(!L){L=E;
E=B.Widget
}B.expr[":"][I.toLowerCase()]=function(M){return !!B.data(M,I)
};
B[H]=B[H]||{};
J=B[H][D];
G=B[H][D]=function(M,N){if(!this._createWidget){return new G(M,N)
}if(arguments.length){this._createWidget(M,N)
}};
B.extend(G,J,{version:L.version,_proto:B.extend({},L),_childConstructors:[]});
K=new E();
K.options=B.widget.extend({},K.options);
B.each(L,function(N,M){if(!B.isFunction(M)){F[N]=M;
return 
}F[N]=(function(){var O=function(){return E.prototype[N].apply(this,arguments)
},P=function(Q){return E.prototype[N].apply(this,Q)
};
return function(){var S=this._super,Q=this._superApply,R;
this._super=O;
this._superApply=P;
R=M.apply(this,arguments);
this._super=S;
this._superApply=Q;
return R
}
})()
});
G.prototype=B.widget.extend(K,{widgetEventPrefix:J?(K.widgetEventPrefix||D):D},F,{constructor:G,namespace:H,widgetName:D,widgetFullName:I});
if(J){B.each(J._childConstructors,function(N,O){var M=O.prototype;
B.widget(M.namespace+"."+M.widgetName,G,O._proto)
});
delete J._childConstructors
}else{E._childConstructors.push(G)
}B.widget.bridge(D,G);
return G
};
B.widget.extend=function(I){var E=A.call(arguments,1),H=0,D=E.length,F,G;
for(;
H<D;
H++){for(F in E[H]){G=E[H][F];
if(E[H].hasOwnProperty(F)&&G!==undefined){if(B.isPlainObject(G)){I[F]=B.isPlainObject(I[F])?B.widget.extend({},I[F],G):B.widget.extend({},G)
}else{I[F]=G
}}}}return I
};
B.widget.bridge=function(E,D){var F=D.prototype.widgetFullName||E;
B.fn[E]=function(I){var G=typeof I==="string",H=A.call(arguments,1),J=this;
I=!G&&H.length?B.widget.extend.apply(null,[I].concat(H)):I;
if(G){this.each(function(){var L,K=B.data(this,F);
if(I==="instance"){J=K;
return false
}if(!K){return B.error("cannot call methods on "+E+" prior to initialization; attempted to call method '"+I+"'")
}if(!B.isFunction(K[I])||I.charAt(0)==="_"){return B.error("no such method '"+I+"' for "+E+" widget instance")
}L=K[I].apply(K,H);
if(L!==K&&L!==undefined){J=L&&L.jquery?J.pushStack(L.get()):L;
return false
}})
}else{this.each(function(){var K=B.data(this,F);
if(K){K.option(I||{});
if(K._init){K._init()
}}else{B.data(this,F,new D(I,this))
}})
}return J
}
};
B.Widget=function(){};
B.Widget._childConstructors=[];
B.Widget.prototype={widgetName:"widget",widgetEventPrefix:"",defaultElement:"<div>",options:{disabled:false,create:null},_createWidget:function(D,E){E=B(E||this.defaultElement||this)[0];
this.element=B(E);
this.uuid=C++;
this.eventNamespace="."+this.widgetName+this.uuid;
this.bindings=B();
this.hoverable=B();
this.focusable=B();
if(E!==this){B.data(E,this.widgetFullName,this);
this._on(true,this.element,{remove:function(F){if(F.target===E){this.destroy()
}}});
this.document=B(E.style?E.ownerDocument:E.document||E);
this.window=B(this.document[0].defaultView||this.document[0].parentWindow)
}this.options=B.widget.extend({},this.options,this._getCreateOptions(),D);
this._create();
this._trigger("create",null,this._getCreateEventData());
this._init()
},_getCreateOptions:B.noop,_getCreateEventData:B.noop,_create:B.noop,_init:B.noop,destroy:function(){this._destroy();
this.element.unbind(this.eventNamespace).removeData(this.widgetFullName).removeData(B.camelCase(this.widgetFullName));
this.widget().unbind(this.eventNamespace).removeAttr("aria-disabled").removeClass(this.widgetFullName+"-disabled ui-state-disabled");
this.bindings.unbind(this.eventNamespace);
this.hoverable.removeClass("ui-state-hover");
this.focusable.removeClass("ui-state-focus")
},_destroy:B.noop,widget:function(){return this.element
},option:function(G,H){var D=G,I,F,E;
if(arguments.length===0){return B.widget.extend({},this.options)
}if(typeof G==="string"){D={};
I=G.split(".");
G=I.shift();
if(I.length){F=D[G]=B.widget.extend({},this.options[G]);
for(E=0;
E<I.length-1;
E++){F[I[E]]=F[I[E]]||{};
F=F[I[E]]
}G=I.pop();
if(arguments.length===1){return F[G]===undefined?null:F[G]
}F[G]=H
}else{if(arguments.length===1){return this.options[G]===undefined?null:this.options[G]
}D[G]=H
}}this._setOptions(D);
return this
},_setOptions:function(D){var E;
for(E in D){this._setOption(E,D[E])
}return this
},_setOption:function(D,E){this.options[D]=E;
if(D==="disabled"){this.widget().toggleClass(this.widgetFullName+"-disabled",!!E);
if(E){this.hoverable.removeClass("ui-state-hover");
this.focusable.removeClass("ui-state-focus")
}}return this
},enable:function(){return this._setOptions({disabled:false})
},disable:function(){return this._setOptions({disabled:true})
},_on:function(G,F,E){var H,D=this;
if(typeof G!=="boolean"){E=F;
F=G;
G=false
}if(!E){E=F;
F=this.element;
H=this.widget()
}else{F=H=B(F);
this.bindings=this.bindings.add(F)
}B.each(E,function(N,M){function K(){if(!G&&(D.options.disabled===true||B(this).hasClass("ui-state-disabled"))){return 
}return(typeof M==="string"?D[M]:M).apply(D,arguments)
}if(typeof M!=="string"){K.guid=M.guid=M.guid||K.guid||B.guid++
}var L=N.match(/^([\w:-]*)\s*(.*)$/),J=L[1]+D.eventNamespace,I=L[2];
if(I){H.delegate(I,J,K)
}else{F.bind(J,K)
}})
},_off:function(E,D){D=(D||"").split(" ").join(this.eventNamespace+" ")+this.eventNamespace;
E.unbind(D).undelegate(D);
this.bindings=B(this.bindings.not(E).get());
this.focusable=B(this.focusable.not(E).get());
this.hoverable=B(this.hoverable.not(E).get())
},_delay:function(G,F){function E(){return(typeof G==="string"?D[G]:G).apply(D,arguments)
}var D=this;
return setTimeout(E,F||0)
},_hoverable:function(D){this.hoverable=this.hoverable.add(D);
this._on(D,{mouseenter:function(E){B(E.currentTarget).addClass("ui-state-hover")
},mouseleave:function(E){B(E.currentTarget).removeClass("ui-state-hover")
}})
},_focusable:function(D){this.focusable=this.focusable.add(D);
this._on(D,{focusin:function(E){B(E.currentTarget).addClass("ui-state-focus")
},focusout:function(E){B(E.currentTarget).removeClass("ui-state-focus")
}})
},_trigger:function(D,E,F){var I,H,G=this.options[D];
F=F||{};
E=B.Event(E);
E.type=(D===this.widgetEventPrefix?D:this.widgetEventPrefix+D).toLowerCase();
E.target=this.element[0];
H=E.originalEvent;
if(H){for(I in H){if(!(I in E)){E[I]=H[I]
}}}this.element.trigger(E,F);
return !(B.isFunction(G)&&G.apply(this.element[0],[E].concat(F))===false||E.isDefaultPrevented())
}};
B.each({show:"fadeIn",hide:"fadeOut"},function(E,D){B.Widget.prototype["_"+E]=function(H,G,J){if(typeof G==="string"){G={effect:G}
}var I,F=!G?E:G===true||typeof G==="number"?D:G.effect||D;
G=G||{};
if(typeof G==="number"){G={duration:G}
}I=!B.isEmptyObject(G);
G.complete=J;
if(G.delay){H.delay(G.delay)
}if(I&&B.effects&&B.effects.effect[F]){H[E](G)
}else{if(F!==E&&H[F]){H[F](G.duration,G.easing,J)
}else{H.queue(function(K){B(this)[E]();
if(J){J.call(H[0])
}K()
})
}}}
});
return B.widget
}));;/*
 * jQuery UI Effects 1.11.2
 * http://jqueryui.com
 *
 * Copyright 2014 jQuery Foundation and other contributors
 * Released under the MIT license.
 * http://jquery.org/license
 *
 * http://api.jqueryui.com/category/effects-core/
 */
(function(A){if(typeof define==="function"&&define.amd){define(["jquery"],A)
}else{A(jQuery)
}}(function($){var dataSpace="ui-effects-",jQuery=$;
$.effects={effect:{}};
/*
 * jQuery Color Animations v2.1.2
 * https://github.com/jquery/jquery-color
 *
 * Copyright 2014 jQuery Foundation and other contributors
 * Released under the MIT license.
 * http://jquery.org/license
 *
 * Date: Wed Jan 16 08:47:09 2013 -0600
 */
(function(jQuery,undefined){var stepHooks="backgroundColor borderBottomColor borderLeftColor borderRightColor borderTopColor color columnRuleColor outlineColor textDecorationColor textEmphasisColor",rplusequals=/^([\-+])=\s*(\d+\.?\d*)/,stringParsers=[{re:/rgba?\(\s*(\d{1,3})\s*,\s*(\d{1,3})\s*,\s*(\d{1,3})\s*(?:,\s*(\d?(?:\.\d+)?)\s*)?\)/,parse:function(execResult){return[execResult[1],execResult[2],execResult[3],execResult[4]]
}},{re:/rgba?\(\s*(\d+(?:\.\d+)?)\%\s*,\s*(\d+(?:\.\d+)?)\%\s*,\s*(\d+(?:\.\d+)?)\%\s*(?:,\s*(\d?(?:\.\d+)?)\s*)?\)/,parse:function(execResult){return[execResult[1]*2.55,execResult[2]*2.55,execResult[3]*2.55,execResult[4]]
}},{re:/#([a-f0-9]{2})([a-f0-9]{2})([a-f0-9]{2})/,parse:function(execResult){return[parseInt(execResult[1],16),parseInt(execResult[2],16),parseInt(execResult[3],16)]
}},{re:/#([a-f0-9])([a-f0-9])([a-f0-9])/,parse:function(execResult){return[parseInt(execResult[1]+execResult[1],16),parseInt(execResult[2]+execResult[2],16),parseInt(execResult[3]+execResult[3],16)]
}},{re:/hsla?\(\s*(\d+(?:\.\d+)?)\s*,\s*(\d+(?:\.\d+)?)\%\s*,\s*(\d+(?:\.\d+)?)\%\s*(?:,\s*(\d?(?:\.\d+)?)\s*)?\)/,space:"hsla",parse:function(execResult){return[execResult[1],execResult[2]/100,execResult[3]/100,execResult[4]]
}}],color=jQuery.Color=function(color,green,blue,alpha){return new jQuery.Color.fn.parse(color,green,blue,alpha)
},spaces={rgba:{props:{red:{idx:0,type:"byte"},green:{idx:1,type:"byte"},blue:{idx:2,type:"byte"}}},hsla:{props:{hue:{idx:0,type:"degrees"},saturation:{idx:1,type:"percent"},lightness:{idx:2,type:"percent"}}}},propTypes={"byte":{floor:true,max:255},percent:{max:1},degrees:{mod:360,floor:true}},support=color.support={},supportElem=jQuery("<p>")[0],colors,each=jQuery.each;
supportElem.style.cssText="background-color:rgba(1,1,1,.5)";
support.rgba=supportElem.style.backgroundColor.indexOf("rgba")>-1;
each(spaces,function(spaceName,space){space.cache="_"+spaceName;
space.props.alpha={idx:3,type:"percent",def:1}
});
function clamp(value,prop,allowEmpty){var type=propTypes[prop.type]||{};
if(value==null){return(allowEmpty||!prop.def)?null:prop.def
}value=type.floor?~~value:parseFloat(value);
if(isNaN(value)){return prop.def
}if(type.mod){return(value+type.mod)%type.mod
}return 0>value?0:type.max<value?type.max:value
}function stringParse(string){var inst=color(),rgba=inst._rgba=[];
string=string.toLowerCase();
each(stringParsers,function(i,parser){var parsed,match=parser.re.exec(string),values=match&&parser.parse(match),spaceName=parser.space||"rgba";
if(values){parsed=inst[spaceName](values);
inst[spaces[spaceName].cache]=parsed[spaces[spaceName].cache];
rgba=inst._rgba=parsed._rgba;
return false
}});
if(rgba.length){if(rgba.join()==="0,0,0,0"){jQuery.extend(rgba,colors.transparent)
}return inst
}return colors[string]
}color.fn=jQuery.extend(color.prototype,{parse:function(red,green,blue,alpha){if(red===undefined){this._rgba=[null,null,null,null];
return this
}if(red.jquery||red.nodeType){red=jQuery(red).css(green);
green=undefined
}var inst=this,type=jQuery.type(red),rgba=this._rgba=[];
if(green!==undefined){red=[red,green,blue,alpha];
type="array"
}if(type==="string"){return this.parse(stringParse(red)||colors._default)
}if(type==="array"){each(spaces.rgba.props,function(key,prop){rgba[prop.idx]=clamp(red[prop.idx],prop)
});
return this
}if(type==="object"){if(red instanceof color){each(spaces,function(spaceName,space){if(red[space.cache]){inst[space.cache]=red[space.cache].slice()
}})
}else{each(spaces,function(spaceName,space){var cache=space.cache;
each(space.props,function(key,prop){if(!inst[cache]&&space.to){if(key==="alpha"||red[key]==null){return 
}inst[cache]=space.to(inst._rgba)
}inst[cache][prop.idx]=clamp(red[key],prop,true)
});
if(inst[cache]&&jQuery.inArray(null,inst[cache].slice(0,3))<0){inst[cache][3]=1;
if(space.from){inst._rgba=space.from(inst[cache])
}}})
}return this
}},is:function(compare){var is=color(compare),same=true,inst=this;
each(spaces,function(_,space){var localCache,isCache=is[space.cache];
if(isCache){localCache=inst[space.cache]||space.to&&space.to(inst._rgba)||[];
each(space.props,function(_,prop){if(isCache[prop.idx]!=null){same=(isCache[prop.idx]===localCache[prop.idx]);
return same
}})
}return same
});
return same
},_space:function(){var used=[],inst=this;
each(spaces,function(spaceName,space){if(inst[space.cache]){used.push(spaceName)
}});
return used.pop()
},transition:function(other,distance){var end=color(other),spaceName=end._space(),space=spaces[spaceName],startColor=this.alpha()===0?color("transparent"):this,start=startColor[space.cache]||space.to(startColor._rgba),result=start.slice();
end=end[space.cache];
each(space.props,function(key,prop){var index=prop.idx,startValue=start[index],endValue=end[index],type=propTypes[prop.type]||{};
if(endValue===null){return 
}if(startValue===null){result[index]=endValue
}else{if(type.mod){if(endValue-startValue>type.mod/2){startValue+=type.mod
}else{if(startValue-endValue>type.mod/2){startValue-=type.mod
}}}result[index]=clamp((endValue-startValue)*distance+startValue,prop)
}});
return this[spaceName](result)
},blend:function(opaque){if(this._rgba[3]===1){return this
}var rgb=this._rgba.slice(),a=rgb.pop(),blend=color(opaque)._rgba;
return color(jQuery.map(rgb,function(v,i){return(1-a)*blend[i]+a*v
}))
},toRgbaString:function(){var prefix="rgba(",rgba=jQuery.map(this._rgba,function(v,i){return v==null?(i>2?1:0):v
});
if(rgba[3]===1){rgba.pop();
prefix="rgb("
}return prefix+rgba.join()+")"
},toHslaString:function(){var prefix="hsla(",hsla=jQuery.map(this.hsla(),function(v,i){if(v==null){v=i>2?1:0
}if(i&&i<3){v=Math.round(v*100)+"%"
}return v
});
if(hsla[3]===1){hsla.pop();
prefix="hsl("
}return prefix+hsla.join()+")"
},toHexString:function(includeAlpha){var rgba=this._rgba.slice(),alpha=rgba.pop();
if(includeAlpha){rgba.push(~~(alpha*255))
}return"#"+jQuery.map(rgba,function(v){v=(v||0).toString(16);
return v.length===1?"0"+v:v
}).join("")
},toString:function(){return this._rgba[3]===0?"transparent":this.toRgbaString()
}});
color.fn.parse.prototype=color.fn;
function hue2rgb(p,q,h){h=(h+1)%1;
if(h*6<1){return p+(q-p)*h*6
}if(h*2<1){return q
}if(h*3<2){return p+(q-p)*((2/3)-h)*6
}return p
}spaces.hsla.to=function(rgba){if(rgba[0]==null||rgba[1]==null||rgba[2]==null){return[null,null,null,rgba[3]]
}var r=rgba[0]/255,g=rgba[1]/255,b=rgba[2]/255,a=rgba[3],max=Math.max(r,g,b),min=Math.min(r,g,b),diff=max-min,add=max+min,l=add*0.5,h,s;
if(min===max){h=0
}else{if(r===max){h=(60*(g-b)/diff)+360
}else{if(g===max){h=(60*(b-r)/diff)+120
}else{h=(60*(r-g)/diff)+240
}}}if(diff===0){s=0
}else{if(l<=0.5){s=diff/add
}else{s=diff/(2-add)
}}return[Math.round(h)%360,s,l,a==null?1:a]
};
spaces.hsla.from=function(hsla){if(hsla[0]==null||hsla[1]==null||hsla[2]==null){return[null,null,null,hsla[3]]
}var h=hsla[0]/360,s=hsla[1],l=hsla[2],a=hsla[3],q=l<=0.5?l*(1+s):l+s-l*s,p=2*l-q;
return[Math.round(hue2rgb(p,q,h+(1/3))*255),Math.round(hue2rgb(p,q,h)*255),Math.round(hue2rgb(p,q,h-(1/3))*255),a]
};
each(spaces,function(spaceName,space){var props=space.props,cache=space.cache,to=space.to,from=space.from;
color.fn[spaceName]=function(value){if(to&&!this[cache]){this[cache]=to(this._rgba)
}if(value===undefined){return this[cache].slice()
}var ret,type=jQuery.type(value),arr=(type==="array"||type==="object")?value:arguments,local=this[cache].slice();
each(props,function(key,prop){var val=arr[type==="object"?key:prop.idx];
if(val==null){val=local[prop.idx]
}local[prop.idx]=clamp(val,prop)
});
if(from){ret=color(from(local));
ret[cache]=local;
return ret
}else{return color(local)
}};
each(props,function(key,prop){if(color.fn[key]){return 
}color.fn[key]=function(value){var vtype=jQuery.type(value),fn=(key==="alpha"?(this._hsla?"hsla":"rgba"):spaceName),local=this[fn](),cur=local[prop.idx],match;
if(vtype==="undefined"){return cur
}if(vtype==="function"){value=value.call(this,cur);
vtype=jQuery.type(value)
}if(value==null&&prop.empty){return this
}if(vtype==="string"){match=rplusequals.exec(value);
if(match){value=cur+parseFloat(match[2])*(match[1]==="+"?1:-1)
}}local[prop.idx]=value;
return this[fn](local)
}
})
});
color.hook=function(hook){var hooks=hook.split(" ");
each(hooks,function(i,hook){jQuery.cssHooks[hook]={set:function(elem,value){var parsed,curElem,backgroundColor="";
if(value!=="transparent"&&(jQuery.type(value)!=="string"||(parsed=stringParse(value)))){value=color(parsed||value);
if(!support.rgba&&value._rgba[3]!==1){curElem=hook==="backgroundColor"?elem.parentNode:elem;
while((backgroundColor===""||backgroundColor==="transparent")&&curElem&&curElem.style){try{backgroundColor=jQuery.css(curElem,"backgroundColor");
curElem=curElem.parentNode
}catch(e){}}value=value.blend(backgroundColor&&backgroundColor!=="transparent"?backgroundColor:"_default")
}value=value.toRgbaString()
}try{elem.style[hook]=value
}catch(e){}}};
jQuery.fx.step[hook]=function(fx){if(!fx.colorInit){fx.start=color(fx.elem,hook);
fx.end=color(fx.end);
fx.colorInit=true
}jQuery.cssHooks[hook].set(fx.elem,fx.start.transition(fx.end,fx.pos))
}
})
};
color.hook(stepHooks);
jQuery.cssHooks.borderColor={expand:function(value){var expanded={};
each(["Top","Right","Bottom","Left"],function(i,part){expanded["border"+part+"Color"]=value
});
return expanded
}};
colors=jQuery.Color.names={aqua:"#00ffff",black:"#000000",blue:"#0000ff",fuchsia:"#ff00ff",gray:"#808080",green:"#008000",lime:"#00ff00",maroon:"#800000",navy:"#000080",olive:"#808000",purple:"#800080",red:"#ff0000",silver:"#c0c0c0",teal:"#008080",white:"#ffffff",yellow:"#ffff00",transparent:[null,null,null,0],_default:"#ffffff"}
})(jQuery);
(function(){var classAnimationActions=["add","remove","toggle"],shorthandStyles={border:1,borderBottom:1,borderColor:1,borderLeft:1,borderRight:1,borderTop:1,borderWidth:1,margin:1,padding:1};
$.each(["borderLeftStyle","borderRightStyle","borderBottomStyle","borderTopStyle"],function(_,prop){$.fx.step[prop]=function(fx){if(fx.end!=="none"&&!fx.setAttr||fx.pos===1&&!fx.setAttr){jQuery.style(fx.elem,prop,fx.end);
fx.setAttr=true
}}
});
function getElementStyles(elem){var key,len,style=elem.ownerDocument.defaultView?elem.ownerDocument.defaultView.getComputedStyle(elem,null):elem.currentStyle,styles={};
if(style&&style.length&&style[0]&&style[style[0]]){len=style.length;
while(len--){key=style[len];
if(typeof style[key]==="string"){styles[$.camelCase(key)]=style[key]
}}}else{for(key in style){if(typeof style[key]==="string"){styles[key]=style[key]
}}}return styles
}function styleDifference(oldStyle,newStyle){var diff={},name,value;
for(name in newStyle){value=newStyle[name];
if(oldStyle[name]!==value){if(!shorthandStyles[name]){if($.fx.step[name]||!isNaN(parseFloat(value))){diff[name]=value
}}}}return diff
}if(!$.fn.addBack){$.fn.addBack=function(selector){return this.add(selector==null?this.prevObject:this.prevObject.filter(selector))
}
}$.effects.animateClass=function(value,duration,easing,callback){var o=$.speed(duration,easing,callback);
return this.queue(function(){var animated=$(this),baseClass=animated.attr("class")||"",applyClassChange,allAnimations=o.children?animated.find("*").addBack():animated;
allAnimations=allAnimations.map(function(){var el=$(this);
return{el:el,start:getElementStyles(this)}
});
applyClassChange=function(){$.each(classAnimationActions,function(i,action){if(value[action]){animated[action+"Class"](value[action])
}})
};
applyClassChange();
allAnimations=allAnimations.map(function(){this.end=getElementStyles(this.el[0]);
this.diff=styleDifference(this.start,this.end);
return this
});
animated.attr("class",baseClass);
allAnimations=allAnimations.map(function(){var styleInfo=this,dfd=$.Deferred(),opts=$.extend({},o,{queue:false,complete:function(){dfd.resolve(styleInfo)
}});
this.el.animate(this.diff,opts);
return dfd.promise()
});
$.when.apply($,allAnimations.get()).done(function(){applyClassChange();
$.each(arguments,function(){var el=this.el;
$.each(this.diff,function(key){el.css(key,"")
})
});
o.complete.call(animated[0])
})
})
};
$.fn.extend({addClass:(function(orig){return function(classNames,speed,easing,callback){return speed?$.effects.animateClass.call(this,{add:classNames},speed,easing,callback):orig.apply(this,arguments)
}
})($.fn.addClass),removeClass:(function(orig){return function(classNames,speed,easing,callback){return arguments.length>1?$.effects.animateClass.call(this,{remove:classNames},speed,easing,callback):orig.apply(this,arguments)
}
})($.fn.removeClass),toggleClass:(function(orig){return function(classNames,force,speed,easing,callback){if(typeof force==="boolean"||force===undefined){if(!speed){return orig.apply(this,arguments)
}else{return $.effects.animateClass.call(this,(force?{add:classNames}:{remove:classNames}),speed,easing,callback)
}}else{return $.effects.animateClass.call(this,{toggle:classNames},force,speed,easing)
}}
})($.fn.toggleClass),switchClass:function(remove,add,speed,easing,callback){return $.effects.animateClass.call(this,{add:add,remove:remove},speed,easing,callback)
}})
})();
(function(){$.extend($.effects,{version:"1.11.2",save:function(element,set){for(var i=0;
i<set.length;
i++){if(set[i]!==null){element.data(dataSpace+set[i],element[0].style[set[i]])
}}},restore:function(element,set){var val,i;
for(i=0;
i<set.length;
i++){if(set[i]!==null){val=element.data(dataSpace+set[i]);
if(val===undefined){val=""
}element.css(set[i],val)
}}},setMode:function(el,mode){if(mode==="toggle"){mode=el.is(":hidden")?"show":"hide"
}return mode
},getBaseline:function(origin,original){var y,x;
switch(origin[0]){case"top":y=0;
break;
case"middle":y=0.5;
break;
case"bottom":y=1;
break;
default:y=origin[0]/original.height
}switch(origin[1]){case"left":x=0;
break;
case"center":x=0.5;
break;
case"right":x=1;
break;
default:x=origin[1]/original.width
}return{x:x,y:y}
},createWrapper:function(element){if(element.parent().is(".ui-effects-wrapper")){return element.parent()
}var props={width:element.outerWidth(true),height:element.outerHeight(true),"float":element.css("float")},wrapper=$("<div></div>").addClass("ui-effects-wrapper").css({fontSize:"100%",background:"transparent",border:"none",margin:0,padding:0}),size={width:element.width(),height:element.height()},active=document.activeElement;
try{active.id
}catch(e){active=document.body
}element.wrap(wrapper);
if(element[0]===active||$.contains(element[0],active)){$(active).focus()
}wrapper=element.parent();
if(element.css("position")==="static"){wrapper.css({position:"relative"});
element.css({position:"relative"})
}else{$.extend(props,{position:element.css("position"),zIndex:element.css("z-index")});
$.each(["top","left","bottom","right"],function(i,pos){props[pos]=element.css(pos);
if(isNaN(parseInt(props[pos],10))){props[pos]="auto"
}});
element.css({position:"relative",top:0,left:0,right:"auto",bottom:"auto"})
}element.css(size);
return wrapper.css(props).show()
},removeWrapper:function(element){var active=document.activeElement;
if(element.parent().is(".ui-effects-wrapper")){element.parent().replaceWith(element);
if(element[0]===active||$.contains(element[0],active)){$(active).focus()
}}return element
},setTransition:function(element,list,factor,value){value=value||{};
$.each(list,function(i,x){var unit=element.cssUnit(x);
if(unit[0]>0){value[x]=unit[0]*factor+unit[1]
}});
return value
}});
function _normalizeArguments(effect,options,speed,callback){if($.isPlainObject(effect)){options=effect;
effect=effect.effect
}effect={effect:effect};
if(options==null){options={}
}if($.isFunction(options)){callback=options;
speed=null;
options={}
}if(typeof options==="number"||$.fx.speeds[options]){callback=speed;
speed=options;
options={}
}if($.isFunction(speed)){callback=speed;
speed=null
}if(options){$.extend(effect,options)
}speed=speed||options.duration;
effect.duration=$.fx.off?0:typeof speed==="number"?speed:speed in $.fx.speeds?$.fx.speeds[speed]:$.fx.speeds._default;
effect.complete=callback||options.complete;
return effect
}function standardAnimationOption(option){if(!option||typeof option==="number"||$.fx.speeds[option]){return true
}if(typeof option==="string"&&!$.effects.effect[option]){return true
}if($.isFunction(option)){return true
}if(typeof option==="object"&&!option.effect){return true
}return false
}$.fn.extend({effect:function(){var args=_normalizeArguments.apply(this,arguments),mode=args.mode,queue=args.queue,effectMethod=$.effects.effect[args.effect];
if($.fx.off||!effectMethod){if(mode){return this[mode](args.duration,args.complete)
}else{return this.each(function(){if(args.complete){args.complete.call(this)
}})
}}function run(next){var elem=$(this),complete=args.complete,mode=args.mode;
function done(){if($.isFunction(complete)){complete.call(elem[0])
}if($.isFunction(next)){next()
}}if(elem.is(":hidden")?mode==="hide":mode==="show"){elem[mode]();
done()
}else{effectMethod.call(elem[0],args,done)
}}return queue===false?this.each(run):this.queue(queue||"fx",run)
},show:(function(orig){return function(option){if(standardAnimationOption(option)){return orig.apply(this,arguments)
}else{var args=_normalizeArguments.apply(this,arguments);
args.mode="show";
return this.effect.call(this,args)
}}
})($.fn.show),hide:(function(orig){return function(option){if(standardAnimationOption(option)){return orig.apply(this,arguments)
}else{var args=_normalizeArguments.apply(this,arguments);
args.mode="hide";
return this.effect.call(this,args)
}}
})($.fn.hide),toggle:(function(orig){return function(option){if(standardAnimationOption(option)||typeof option==="boolean"){return orig.apply(this,arguments)
}else{var args=_normalizeArguments.apply(this,arguments);
args.mode="toggle";
return this.effect.call(this,args)
}}
})($.fn.toggle),cssUnit:function(key){var style=this.css(key),val=[];
$.each(["em","px","%","pt"],function(i,unit){if(style.indexOf(unit)>0){val=[parseFloat(style),unit]
}});
return val
}})
})();
(function(){var baseEasings={};
$.each(["Quad","Cubic","Quart","Quint","Expo"],function(i,name){baseEasings[name]=function(p){return Math.pow(p,i+2)
}
});
$.extend(baseEasings,{Sine:function(p){return 1-Math.cos(p*Math.PI/2)
},Circ:function(p){return 1-Math.sqrt(1-p*p)
},Elastic:function(p){return p===0||p===1?p:-Math.pow(2,8*(p-1))*Math.sin(((p-1)*80-7.5)*Math.PI/15)
},Back:function(p){return p*p*(3*p-2)
},Bounce:function(p){var pow2,bounce=4;
while(p<((pow2=Math.pow(2,--bounce))-1)/11){}return 1/Math.pow(4,3-bounce)-7.5625*Math.pow((pow2*3-2)/22-p,2)
}});
$.each(baseEasings,function(name,easeIn){$.easing["easeIn"+name]=easeIn;
$.easing["easeOut"+name]=function(p){return 1-easeIn(1-p)
};
$.easing["easeInOut"+name]=function(p){return p<0.5?easeIn(p*2)/2:1-easeIn(p*-2+2)/2
}
})
})();
return $.effects
}));;(function(C,B){B.ui=B.ui||{};
var A={disabled:false,selectable:true,unselectable:false,mode:"client",stylePrefix:"rf-pm-itm",itemStep:20};
var E={exec:function(G){if(G.expanded){var F=G.options.expandEvent==G.options.collapseEvent&&G.options.collapseEvent=="click";
if(F&&G.__fireEvent("beforeswitch")==false){return false
}if(!G.expanded()){if(G.options.expandEvent=="click"&&G.__fireEvent("beforeexpand")==false){return false
}}else{if(G.options.collapseEvent=="click"&&G.__fireEvent("beforecollapse")==false){return false
}}}var H=G.mode;
if(H=="server"){return this.execServer(G)
}else{if(H=="ajax"){return this.execAjax(G)
}else{if(H=="client"||H=="none"){return this.execClient(G)
}else{B.log.error("SELECT_ITEM.exec : unknown mode ("+H+")")
}}}},execServer:function(F){F.__changeState();
var G={};
G[F.__panelMenu().id]=F.itemName;
G[F.id]=F.id;
C.extend(G,F.options.ajax["parameters"]||{});
B.submitForm(this.__getParentForm(F),G);
return false
},execAjax:function(F){var G=F.__changeState();
B.ajax(F.id,null,C.extend({},F.options.ajax,{}));
F.__restoreState(G);
return true
},execClient:function(I){var H=I.__rfPanelMenu();
var G=H.getSelectedItem();
if(G){G.unselect()
}H.selectedItem(I.itemName);
I.__select();
var F=I.__fireSelect();
if(I.__switch){var J=I.mode;
if(J=="client"||J=="none"){I.__switch(!I.expanded())
}}return F
},__getParentForm:function(F){return C(C(B.getDomElement(F.id)).parents("form")[0])
}};
B.ui.PanelMenuItem=B.BaseComponent.extendClass({name:"PanelMenuItem",init:function(H,G){D.constructor.call(this,H);
var F=C(this.attachToDom());
this.options=C.extend(this.options,A,G||{});
this.mode=this.options.mode;
this.itemName=this.options.name;
var I=this.__rfPanelMenu();
I.addItem(this);
this.selectionClass=this.options.stylePrefix+"-sel";
if(!this.options.disabled){var J=this;
if(this.options.selectable){this.__header().bind("click",function(){if(J.__rfPanelMenu().selectedItem()==J.id){if(J.options.unselectable){return J.unselect()
}}else{return J.select()
}})
}}J=this;
C(this.__panelMenu()).ready(function(){J.__renderNestingLevel()
});
this.__addUserEventHandler("select");
this.__addUserEventHandler("beforeselect")
},selected:function(){return this.__header().hasClass(this.selectionClass)
},select:function(){var F=this.__fireBeforeSelect();
if(!F){return false
}return E.exec(this)
},onCompleteHandler:function(){E.execClient(this)
},unselect:function(){var F=this.__rfPanelMenu();
if(F.selectedItem()==this.itemName){F.selectedItem(null)
}else{B.log.warn("You tried to unselect item (name="+this.itemName+") that isn't seleted")
}this.__unselect();
return this.__fireUnselect()
},__rfParentItem:function(){var F=this.__item().parents(".rf-pm-gr")[0];
if(!F){F=this.__item().parents(".rf-pm-top-gr")[0]
}if(!F){F=this.__panelMenu()
}return F?B.component(F):null
},__getNestingLevel:function(){if(!this.nestingLevel){var F=this.__rfParentItem();
if(F&&F.__getNestingLevel){this.nestingLevel=F.__getNestingLevel()+1
}else{this.nestingLevel=0
}}return this.nestingLevel
},__renderNestingLevel:function(){this.__item().find("td").first().css("padding-left",this.options.itemStep*this.__getNestingLevel())
},__panelMenu:function(){return this.__item().parents(".rf-pm")[0]
},__rfPanelMenu:function(){return B.component(this.__panelMenu())
},__changeState:function(){return this.__rfPanelMenu().selectedItem(this.itemName)
},__restoreState:function(F){if(F){this.__rfPanelMenu().selectedItem(F)
}},__item:function(){return C(B.getDomElement(this.id))
},__header:function(){return this.__item()
},__isSelected:function(){return this.__header().hasClass(this.selectionClass)
},__select:function(){this.__header().addClass(this.selectionClass)
},__unselect:function(){this.__header().removeClass(this.selectionClass)
},__fireBeforeSelect:function(){return B.Event.fireById(this.id,"beforeselect",{item:this})
},__fireSelect:function(){return B.Event.fireById(this.id,"select",{item:this})
},__fireUnselect:function(){return B.Event.fireById(this.id,"unselect",{item:this})
},__fireEvent:function(F,G){return this.invokeEvent(F,B.getDomElement(this.id),G,{id:this.id,item:this})
},__addUserEventHandler:function(F){var G=this.options["on"+F];
if(G){B.Event.bindById(this.id,F,G)
}},__rfTopGroup:function(){var F=this.__item().parents(".rf-pm-top-gr")[0];
return F?F:null
},destroy:function(){var F=this.__rfPanelMenu();
if(F){F.deleteItem(this)
}D.destroy.call(this)
}});
var D=B.ui.PanelMenuItem.$super
})(RichFaces.jQuery,RichFaces);;(function(D,I){var J,A;
var E;
var B;
D.extend({pnotify_remove_all:function(){var L=E.data("pnotify");
if(L&&L.length){D.each(L,function(){if(this.pnotify_remove){this.pnotify_remove()
}})
}},pnotify_position_all:function(){if(A){clearTimeout(A)
}A=null;
var L=E.data("pnotify");
if(!L||!L.length){return 
}D.each(L,function(){var P=this.opts.pnotify_stack;
if(!P){return 
}if(!P.nextpos1){P.nextpos1=P.firstpos1
}if(!P.nextpos2){P.nextpos2=P.firstpos2
}if(!P.addpos2){P.addpos2=0
}if(this.css("display")!="none"){var R,Q;
var M={};
var O;
switch(P.dir1){case"down":O="top";
break;
case"up":O="bottom";
break;
case"left":O="right";
break;
case"right":O="left";
break
}R=parseInt(this.css(O));
if(isNaN(R)){R=0
}if(typeof P.firstpos1=="undefined"){P.firstpos1=R;
P.nextpos1=P.firstpos1
}var N;
switch(P.dir2){case"down":N="top";
break;
case"up":N="bottom";
break;
case"left":N="right";
break;
case"right":N="left";
break
}Q=parseInt(this.css(N));
if(isNaN(Q)){Q=0
}if(typeof P.firstpos2=="undefined"){P.firstpos2=Q;
P.nextpos2=P.firstpos2
}if((P.dir1=="down"&&P.nextpos1+this.height()>B.height())||(P.dir1=="up"&&P.nextpos1+this.height()>B.height())||(P.dir1=="left"&&P.nextpos1+this.width()>B.width())||(P.dir1=="right"&&P.nextpos1+this.width()>B.width())){P.nextpos1=P.firstpos1;
P.nextpos2+=P.addpos2+10;
P.addpos2=0
}if(P.animation&&P.nextpos2<Q){switch(P.dir2){case"down":M.top=P.nextpos2+"px";
break;
case"up":M.bottom=P.nextpos2+"px";
break;
case"left":M.right=P.nextpos2+"px";
break;
case"right":M.left=P.nextpos2+"px";
break
}}else{this.css(N,P.nextpos2+"px")
}switch(P.dir2){case"down":case"up":if(this.outerHeight(true)>P.addpos2){P.addpos2=this.height()
}break;
case"left":case"right":if(this.outerWidth(true)>P.addpos2){P.addpos2=this.width()
}break
}if(P.nextpos1){if(P.animation&&(R>P.nextpos1||M.top||M.bottom||M.right||M.left)){switch(P.dir1){case"down":M.top=P.nextpos1+"px";
break;
case"up":M.bottom=P.nextpos1+"px";
break;
case"left":M.right=P.nextpos1+"px";
break;
case"right":M.left=P.nextpos1+"px";
break
}}else{this.css(O,P.nextpos1+"px")
}}if(M.top||M.bottom||M.right||M.left){this.animate(M,{duration:500,queue:false})
}switch(P.dir1){case"down":case"up":P.nextpos1+=this.height()+10;
break;
case"left":case"right":P.nextpos1+=this.width()+10;
break
}}});
D.each(L,function(){var M=this.opts.pnotify_stack;
if(!M){return 
}M.nextpos1=M.firstpos1;
M.nextpos2=M.firstpos2;
M.addpos2=0;
M.animation=true
})
},pnotify:function(S){if(!E){E=D("body")
}if(!B){B=D(window)
}var T;
var L;
if(typeof S!="object"){L=D.extend({},D.pnotify.defaults);
L.pnotify_text=S
}else{L=D.extend({},D.pnotify.defaults,S);
if(L.pnotify_animation instanceof Object){L.pnotify_animation=D.extend({effect_in:D.pnotify.defaults.pnotify_animation,effect_out:D.pnotify.defaults.pnotify_animation},L.pnotify_animation)
}}if(L.pnotify_before_init){if(L.pnotify_before_init(L)===false){return null
}}var M;
var N=function(Y,V){P.css("display","none");
var U=document.elementFromPoint(Y.clientX,Y.clientY);
P.css("display","block");
var X=D(U);
var W=X.css("cursor");
P.css("cursor",W!="auto"?W:"default");
if(!M||M.get(0)!=U){if(M){F.call(M.get(0),"mouseleave",Y.originalEvent);
F.call(M.get(0),"mouseout",Y.originalEvent)
}F.call(U,"mouseenter",Y.originalEvent);
F.call(U,"mouseover",Y.originalEvent)
}F.call(U,V,Y.originalEvent);
M=X
};
var P=D("<div />",{"class":"rf-ntf "+L.pnotify_addclass,css:{display:"none"},mouseenter:function(U){if(L.pnotify_nonblock){U.stopPropagation()
}if(L.pnotify_mouse_reset&&T=="out"){P.stop(true);
T="in";
P.css("height","auto").animate({width:L.pnotify_width,opacity:L.pnotify_nonblock?L.pnotify_nonblock_opacity:L.pnotify_opacity},"fast")
}if(L.pnotify_nonblock){P.animate({opacity:L.pnotify_nonblock_opacity},"fast")
}if(L.pnotify_hide&&L.pnotify_mouse_reset){P.pnotify_cancel_remove()
}if(L.pnotify_closer&&!L.pnotify_nonblock){P.closer.css("visibility","visible")
}},mouseleave:function(U){if(L.pnotify_nonblock){U.stopPropagation()
}M=null;
P.css("cursor","auto");
if(L.pnotify_nonblock&&T!="out"){P.animate({opacity:L.pnotify_opacity},"fast")
}if(L.pnotify_hide&&L.pnotify_mouse_reset){P.pnotify_queue_remove()
}P.closer.css("visibility","hidden");
D.pnotify_position_all()
},mouseover:function(U){if(L.pnotify_nonblock){U.stopPropagation()
}},mouseout:function(U){if(L.pnotify_nonblock){U.stopPropagation()
}},mousemove:function(U){if(L.pnotify_nonblock){U.stopPropagation();
N(U,"onmousemove")
}},mousedown:function(U){if(L.pnotify_nonblock){U.stopPropagation();
U.preventDefault();
N(U,"onmousedown")
}},mouseup:function(U){if(L.pnotify_nonblock){U.stopPropagation();
U.preventDefault();
N(U,"onmouseup")
}},click:function(U){if(L.pnotify_nonblock){U.stopPropagation();
N(U,"onclick")
}},dblclick:function(U){if(L.pnotify_nonblock){U.stopPropagation();
N(U,"ondblclick")
}}});
P.opts=L;
if(L.pnotify_shadow&&!I.browser.msie){P.shadow_container=D("<div />",{"class":"rf-ntf-shdw"}).prependTo(P)
}P.container=D("<div />",{"class":"rf-ntf-cnt"}).appendTo(P);
P.pnotify_version="1.0.2";
P.pnotify=function(U){var V=L;
if(typeof U=="string"){L.pnotify_text=U
}else{L=D.extend({},L,U)
}P.opts=L;
if(L.pnotify_shadow!=V.pnotify_shadow){if(L.pnotify_shadow&&!I.browser.msie){P.shadow_container=D("<div />",{"class":"rf-ntf-shdw"}).prependTo(P)
}else{P.children(".rf-ntf-shdw").remove()
}}if(L.pnotify_addclass===false){P.removeClass(V.pnotify_addclass)
}else{if(L.pnotify_addclass!==V.pnotify_addclass){P.removeClass(V.pnotify_addclass).addClass(L.pnotify_addclass)
}}if(L.pnotify_title===false){P.title_container.hide("fast")
}else{if(L.pnotify_title!==V.pnotify_title){P.title_container.html(L.pnotify_title).show(200)
}}if(L.pnotify_text===false){P.text_container.hide("fast")
}else{if(L.pnotify_text!==V.pnotify_text){if(L.pnotify_insert_brs){L.pnotify_text=L.pnotify_text.replace(/\n/g,"<br />")
}P.text_container.html(L.pnotify_text).show(200)
}}P.pnotify_history=L.pnotify_history;
if(L.pnotify_type!=V.pnotify_type){P.container.toggleClass("rf-ntf-cnt rf-ntf-cnt-hov")
}if((L.pnotify_notice_icon!=V.pnotify_notice_icon&&L.pnotify_type=="notice")||(L.pnotify_error_icon!=V.pnotify_error_icon&&L.pnotify_type=="error")||(L.pnotify_type!=V.pnotify_type)){P.container.find("div.rf-ntf-ico").remove();
D("<div />",{"class":"rf-ntf-ico"}).append(D("<span />",{"class":L.pnotify_type=="error"?L.pnotify_error_icon:L.pnotify_notice_icon})).prependTo(P.container)
}if(L.pnotify_width!==V.pnotify_width){P.animate({width:L.pnotify_width})
}if(L.pnotify_min_height!==V.pnotify_min_height){P.container.animate({minHeight:L.pnotify_min_height})
}if(L.pnotify_opacity!==V.pnotify_opacity){P.fadeTo(L.pnotify_animate_speed,L.pnotify_opacity)
}if(!L.pnotify_hide){P.pnotify_cancel_remove()
}else{if(!V.pnotify_hide){P.pnotify_queue_remove()
}}P.pnotify_queue_position();
return P
};
P.pnotify_queue_position=function(){if(A){clearTimeout(A)
}A=setTimeout(D.pnotify_position_all,10)
};
P.pnotify_display=function(){if(!P.parent().length){P.appendTo(E)
}if(L.pnotify_before_open){if(L.pnotify_before_open(P)===false){return 
}}P.pnotify_queue_position();
if(L.pnotify_animation=="fade"||L.pnotify_animation.effect_in=="fade"){P.show().fadeTo(0,0).hide()
}else{if(L.pnotify_opacity!=1){P.show().fadeTo(0,L.pnotify_opacity).hide()
}}P.animate_in(function(){if(L.pnotify_after_open){L.pnotify_after_open(P)
}P.pnotify_queue_position();
if(L.pnotify_hide){P.pnotify_queue_remove()
}})
};
P.pnotify_remove=function(){if(P.timer){window.clearTimeout(P.timer);
P.timer=null
}if(L.pnotify_before_close){if(L.pnotify_before_close(P)===false){return 
}}P.animate_out(function(){if(L.pnotify_after_close){if(L.pnotify_after_close(P)===false){return 
}}P.pnotify_queue_position();
if(L.pnotify_remove){P.detach()
}})
};
P.animate_in=function(V){T="in";
var U;
if(typeof L.pnotify_animation.effect_in!="undefined"){U=L.pnotify_animation.effect_in
}else{U=L.pnotify_animation
}if(U=="none"){P.show();
V()
}else{if(U=="show"){P.show(L.pnotify_animate_speed,V)
}else{if(U=="fade"){P.show().fadeTo(L.pnotify_animate_speed,L.pnotify_opacity,V)
}else{if(U=="slide"){P.slideDown(L.pnotify_animate_speed,V)
}else{if(typeof U=="function"){U("in",V,P)
}else{if(P.effect){P.effect(U,{},L.pnotify_animate_speed,V)
}}}}}}};
P.animate_out=function(V){T="out";
var U;
if(typeof L.pnotify_animation.effect_out!="undefined"){U=L.pnotify_animation.effect_out
}else{U=L.pnotify_animation
}if(U=="none"){P.hide();
V()
}else{if(U=="show"){P.hide(L.pnotify_animate_speed,V)
}else{if(U=="fade"){P.fadeOut(L.pnotify_animate_speed,V)
}else{if(U=="slide"){P.slideUp(L.pnotify_animate_speed,V)
}else{if(typeof U=="function"){U("out",V,P)
}else{if(P.effect){P.effect(U,{},L.pnotify_animate_speed,V)
}}}}}}};
P.pnotify_cancel_remove=function(){if(P.timer){window.clearTimeout(P.timer)
}};
P.pnotify_queue_remove=function(){P.pnotify_cancel_remove();
P.timer=window.setTimeout(function(){P.pnotify_remove()
},(isNaN(L.pnotify_delay)?0:L.pnotify_delay))
};
P.closer=D("<div />",{"class":"rf-ntf-cls",css:{cursor:"pointer",visibility:"hidden"},click:function(){P.pnotify_remove();
P.closer.css("visibility","hidden")
}}).append(D("<span />",{"class":"rf-ntf-cls-ico"})).appendTo(P.container);
D("<div />",{"class":"rf-ntf-ico"}).append(D("<span />",{"class":L.pnotify_type=="error"?L.pnotify_error_icon:L.pnotify_notice_icon})).appendTo(P.container);
P.title_container=D("<div />",{"class":"rf-ntf-sum",html:L.pnotify_title}).appendTo(P.container);
if(L.pnotify_title===false){P.title_container.hide()
}if(L.pnotify_insert_brs&&typeof L.pnotify_text=="string"){L.pnotify_text=L.pnotify_text.replace(/\n/g,"<br />")
}P.text_container=D("<div />",{"class":"rf-ntf-det",html:L.pnotify_text}).appendTo(P.container);
if(L.pnotify_text===false){P.text_container.hide()
}D("<div />",{"class":"rf-ntf-clr"}).appendTo(P.container);
if(typeof L.pnotify_width=="string"){P.css("width",L.pnotify_width)
}if(typeof L.pnotify_min_height=="string"){P.container.css("min-height",L.pnotify_min_height)
}P.pnotify_history=L.pnotify_history;
var R=E.data("pnotify");
if(R==null||typeof R!="object"){R=[]
}if(L.pnotify_stack.push=="top"){R=D.merge([P],R)
}else{R=D.merge(R,[P])
}E.data("pnotify",R);
if(L.pnotify_after_init){L.pnotify_after_init(P)
}if(L.pnotify_history){var Q=E.data("pnotify_history");
if(typeof Q=="undefined"){Q=D("<div />",{"class":"rf-ntf-hstr",mouseleave:function(){Q.animate({top:"-"+J+"px"},{duration:100,queue:false})
}}).append(D("<div />",{"class":"rf-ntf-hstr-hdr",text:"Redisplay"})).append(D("<button />",{"class":"rf-ntf-hstr-all",text:"All",click:function(){D.each(E.data("pnotify"),function(){if(this.pnotify_history&&this.pnotify_display){this.pnotify_display()
}});
return false
}})).append(D("<button />",{"class":"rf-ntf-hstr-last",text:"Last",click:function(){var U=1;
var V=E.data("pnotify");
while(!V[V.length-U]||!V[V.length-U].pnotify_history||V[V.length-U].is(":visible")){if(V.length-U===0){return false
}U++
}var W=V[V.length-U];
if(W.pnotify_display){W.pnotify_display()
}return false
}})).appendTo(E);
var O=D("<span />",{"class":"rf-ntf-hstr-hndl",mouseenter:function(){Q.animate({top:"0"},{duration:100,queue:false})
}}).appendTo(Q);
J=O.offset().top+2;
Q.css({top:"-"+J+"px"});
E.data("pnotify_history",Q)
}}L.pnotify_stack.animation=false;
P.pnotify_display();
return P
}});
var K=/^on/;
var C=/^(dbl)?click$|^mouse(move|down|up|over|out|enter|leave)$|^contextmenu$/;
var H=/^(focus|blur|select|change|reset)$|^key(press|down|up)$/;
var G=/^(scroll|resize|(un)?load|abort|error)$/;
var F=function(M,L){var N;
M=M.toLowerCase();
if(document.createEvent&&this.dispatchEvent){M=M.replace(K,"");
if(M.match(C)){D(this).offset();
N=document.createEvent("MouseEvents");
N.initMouseEvent(M,L.bubbles,L.cancelable,L.view,L.detail,L.screenX,L.screenY,L.clientX,L.clientY,L.ctrlKey,L.altKey,L.shiftKey,L.metaKey,L.button,L.relatedTarget)
}else{if(M.match(H)){N=document.createEvent("UIEvents");
N.initUIEvent(M,L.bubbles,L.cancelable,L.view,L.detail)
}else{if(M.match(G)){N=document.createEvent("HTMLEvents");
N.initEvent(M,L.bubbles,L.cancelable)
}}}if(!N){return 
}this.dispatchEvent(N)
}else{if(!M.match(K)){M="on"+M
}N=document.createEventObject(L);
this.fireEvent(M,N)
}};
D.pnotify.defaults={pnotify_title:false,pnotify_text:false,pnotify_addclass:"",pnotify_nonblock:false,pnotify_nonblock_opacity:0.2,pnotify_history:true,pnotify_width:"300px",pnotify_min_height:"16px",pnotify_type:"notice",pnotify_notice_icon:"",pnotify_error_icon:"",pnotify_animation:"fade",pnotify_animate_speed:"slow",pnotify_opacity:1,pnotify_shadow:false,pnotify_closer:true,pnotify_hide:true,pnotify_delay:8000,pnotify_mouse_reset:true,pnotify_remove:true,pnotify_insert_brs:true,pnotify_stack:{dir1:"down",dir2:"left",push:"bottom"}}
})(jQuery,RichFaces);;(function(D,C){C.ui=C.ui||{};
var A={enabledInInput:false,preventDefault:true};
var B=["keydown","keyup"];
C.ui.HotKey=function(G,F){E.constructor.call(this,G);
this.namespace=this.namespace||"."+C.Event.createNamespace(this.name,this.id);
this.attachToDom(this.componentId);
this.options=D.extend({},A,F);
this.__handlers={};
this.options.selector=(this.options.selector)?this.options.selector:document;
D(document).ready(D.proxy(function(){this.__bindDefinedHandlers()
},this))
};
C.BaseComponent.extend(C.ui.HotKey);
var E=C.ui.HotKey.$super;
D.extend(C.ui.HotKey.prototype,{name:"HotKey",__bindDefinedHandlers:function(){for(var F=0;
F<B.length;
F++){if(this.options["on"+B[F]]){this.__bindHandler(B[F])
}}},__bindHandler:function(F){this.__handlers[F]=D.proxy(function(H){var G=this.invokeEvent.call(this,F,document.getElementById(this.id),H);
if(this.options.preventDefault){H.stopPropagation();
H.preventDefault();
return false
}return G
},this);
D(this.options.selector).bind(F+this.namespace,this.options,this.__handlers[F])
},destroy:function(){C.Event.unbindById(this.id,this.namespace);
for(var F in this.__handlers){if(this.__handlers.hasOwnProperty(F)){D(this.options.selector).unbind(F+this.namespace,this.__handlers[F])
}}E.destroy.call(this)
}})
})(RichFaces.jQuery,RichFaces);;(function(B,A){A.ui=A.ui||{};
A.ui.TogglePanelItem=A.BaseComponent.extendClass({name:"TogglePanelItem",init:function(E,D){C.constructor.call(this,E);
this.attachToDom(this.id);
this.options=B.extend(this.options,D||{});
this.name=this.options.name;
this.togglePanelId=this.options.togglePanelId;
this.switchMode=this.options.switchMode;
this.disabled=this.options.disabled||false;
this.index=D.index;
this.getTogglePanel().getItems()[this.index]=this;
this.__addUserEventHandler("enter");
this.__addUserEventHandler("leave")
},getName:function(){return this.options.name
},getTogglePanel:function(){return A.component(this.togglePanelId)
},isSelected:function(){return this.getName()==this.getTogglePanel().getSelectItem()
},__addUserEventHandler:function(D){var E=this.options["on"+D];
if(E){A.Event.bindById(this.id,D,E)
}},__enter:function(){A.getDomElement(this.id).style.display="block";
return this.__fireEnter()
},__leave:function(){var D=this.__fireLeave();
if(!D){return false
}A.getDomElement(this.id).style.display="none";
return true
},__fireLeave:function(){return A.Event.fireById(this.id,"leave")
},__fireEnter:function(){return A.Event.fireById(this.id,"enter")
},destroy:function(){var D=this.getTogglePanel();
if(D){delete D.getItems()[this.index]
}C.destroy.call(this)
}});
var C=A.ui.TogglePanelItem.$super
})(RichFaces.jQuery,RichFaces);;(function(A){if(typeof define==="function"&&define.amd){define(["jquery"],A)
}else{A(jQuery)
}}(function(B){B(window).bind("unload.atmosphere",function(){B.atmosphere.unsubscribe()
});
B(window).bind("offline",function(){var C=[].concat(B.atmosphere.requests);
for(var E=0;
E<C.length;
E++){var D=C[E];
D.close();
clearTimeout(D.response.request.id);
if(D.heartbeatTimer){clearTimeout(D.heartbeatTimer)
}}});
B(window).bind("online",function(){if(B.atmosphere.requests.length>0){for(var C=0;
C<B.atmosphere.requests.length;
C++){B.atmosphere.requests[C].execute()
}}});
B(window).keypress(function(C){if(C.keyCode===27){C.preventDefault()
}});
var A=function(D){var C,F=/^(.*?):[ \t]*([^\r\n]*)\r?$/mg,E={};
while(C=F.exec(D)){E[C[1]]=C[2]
}return E
};
B.atmosphere={version:"2.2.5-jquery",uuid:0,requests:[],callbacks:[],onError:function(C){},onClose:function(C){},onOpen:function(C){},onMessage:function(C){},onReconnect:function(D,C){},onMessagePublished:function(C){},onTransportFailure:function(D,C){},onLocalMessage:function(C){},onClientTimeout:function(C){},onFailureToReconnect:function(D,C){},WebsocketApiAdapter:function(D){var C,E;
D.onMessage=function(F){E.onmessage({data:F.responseBody})
};
D.onMessagePublished=function(F){E.onmessage({data:F.responseBody})
};
D.onOpen=function(F){E.onopen(F)
};
E={close:function(){C.close()
},send:function(F){C.push(F)
},onmessage:function(F){},onopen:function(F){},onclose:function(F){},onerror:function(F){}};
C=new $.atmosphere.subscribe(D);
return E
},AtmosphereRequest:function(v){var K={timeout:300000,method:"GET",headers:{},contentType:"",callback:null,url:"",data:"",suspend:true,maxRequest:-1,reconnect:true,maxStreamingLength:10000000,lastIndex:0,logLevel:"info",requestCount:0,fallbackMethod:"GET",fallbackTransport:"streaming",transport:"long-polling",webSocketImpl:null,webSocketBinaryType:null,dispatchUrl:null,webSocketPathDelimiter:"@@",enableXDR:false,rewriteURL:false,attachHeadersAsQueryString:true,executeCallbackBeforeReconnect:false,readyState:0,withCredentials:false,trackMessageLength:false,messageDelimiter:"|",connectTimeout:-1,reconnectInterval:0,dropHeaders:true,uuid:0,shared:false,readResponsesHeaders:false,maxReconnectOnClose:5,enableProtocol:true,pollingInterval:0,heartbeat:{client:null,server:null},ackInterval:0,closeAsync:false,reconnectOnServerError:true,onError:function(AX){},onClose:function(AX){},onOpen:function(AX){},onMessage:function(AX){},onReopen:function(AY,AX){},onReconnect:function(AY,AX){},onMessagePublished:function(AX){},onTransportFailure:function(AY,AX){},onLocalMessage:function(AX){},onFailureToReconnect:function(AY,AX){},onClientTimeout:function(AX){}};
var AI={status:200,reasonPhrase:"OK",responseBody:"",messages:[],headers:[],state:"messageReceived",transport:"polling",error:null,request:null,partialMessage:"",errorHandled:false,closedByClientTimeout:false,ffTryingReconnect:false};
var AM=null;
var y=null;
var T=null;
var I=null;
var q=null;
var P=true;
var AO=0;
var AC=" ";
var AG=false;
var j=null;
var C;
var AN=null;
var k=B.now();
var S;
var AW;
AF(v);
function AB(){P=true;
AG=false;
AO=0;
AM=null;
y=null;
T=null;
I=null
}function n(){F();
AB()
}function AF(AX){n();
K=B.extend(K,AX);
K.mrequest=K.reconnect;
if(!K.reconnect){K.reconnect=true
}}function AK(){return K.webSocketImpl!=null||window.WebSocket||window.MozWebSocket
}function AJ(){return window.EventSource
}function s(){if(K.shared){AN=AU(K);
if(AN!=null){if(K.logLevel==="debug"){B.atmosphere.debug("Storage service available. All communication will be local")
}if(AN.open(K)){return 
}}if(K.logLevel==="debug"){B.atmosphere.debug("No Storage service available.")
}AN=null
}K.firstMessage=B.atmosphere.uuid==0?true:false;
K.isOpen=false;
K.ctime=B.now();
if(K.uuid===0){K.uuid=B.atmosphere.uuid
}K.closedByClientTimeout=false;
if(K.transport!=="websocket"&&K.transport!=="sse"){e(K)
}else{if(K.transport==="websocket"){if(!AK()){AP("Websocket is not supported, using request.fallbackTransport ("+K.fallbackTransport+")")
}else{x(false)
}}else{if(K.transport==="sse"){if(!AJ()){AP("Server Side Events(SSE) is not supported, using request.fallbackTransport ("+K.fallbackTransport+")")
}else{V(false)
}}}}}function AU(Ab){var Ae,AY,Aa,AZ="atmosphere-"+Ab.url,AX={storage:function(){if(!B.atmosphere.supportStorage()){return 
}var Ah=window.localStorage,Af=function(Ai){return B.parseJSON(Ah.getItem(AZ+"-"+Ai))
},Ag=function(Ai,Aj){Ah.setItem(AZ+"-"+Ai,B.stringifyJSON(Aj))
};
return{init:function(){Ag("children",Af("children").concat([k]));
B(window).on("storage.socket",function(Ai){Ai=Ai.originalEvent;
if(Ai.key===AZ&&Ai.newValue){Ad(Ai.newValue)
}});
return Af("opened")
},signal:function(Ai,Aj){Ah.setItem(AZ,B.stringifyJSON({target:"p",type:Ai,data:Aj}))
},close:function(){var Ai,Aj=Af("children");
B(window).off("storage.socket");
if(Aj){Ai=B.inArray(Ab.id,Aj);
if(Ai>-1){Aj.splice(Ai,1);
Ag("children",Aj)
}}}}
},windowref:function(){var Af=window.open("",AZ.replace(/\W/g,""));
if(!Af||Af.closed||!Af.callbacks){return 
}return{init:function(){Af.callbacks.push(Ad);
Af.children.push(k);
return Af.opened
},signal:function(Ag,Ah){if(!Af.closed&&Af.fire){Af.fire(B.stringifyJSON({target:"p",type:Ag,data:Ah}))
}},close:function(){function Ag(Aj,Ai){var Ah=B.inArray(Ai,Aj);
if(Ah>-1){Aj.splice(Ah,1)
}}if(!Aa){Ag(Af.callbacks,Ad);
Ag(Af.children,k)
}}}
}};
function Ad(Af){var Ah=B.parseJSON(Af),Ag=Ah.data;
if(Ah.target==="c"){switch(Ah.type){case"open":o("opening","local",K);
break;
case"close":if(!Aa){Aa=true;
if(Ag.reason==="aborted"){a()
}else{if(Ag.heir===k){s()
}else{setTimeout(function(){s()
},100)
}}}break;
case"message":G(Ag,"messageReceived",200,Ab.transport);
break;
case"localMessage":Y(Ag);
break
}}}function Ac(){var Af=new RegExp("(?:^|; )("+encodeURIComponent(AZ)+")=([^;]*)").exec(document.cookie);
if(Af){return B.parseJSON(decodeURIComponent(Af[2]))
}}Ae=Ac();
if(!Ae||B.now()-Ae.ts>1000){return 
}AY=AX.storage()||AX.windowref();
if(!AY){return 
}return{open:function(){var Af;
S=setInterval(function(){var Ag=Ae;
Ae=Ac();
if(!Ae||Ag.ts===Ae.ts){Ad(B.stringifyJSON({target:"c",type:"close",data:{reason:"error",heir:Ag.heir}}))
}},1000);
Af=AY.init();
if(Af){setTimeout(function(){o("opening","local",Ab)
},50)
}return Af
},send:function(Af){AY.signal("send",Af)
},localSend:function(Af){AY.signal("localSend",B.stringifyJSON({id:k,event:Af}))
},close:function(){if(!AG){clearInterval(S);
AY.signal("close");
AY.close()
}}}
}function AV(){var AY,AX="atmosphere-"+K.url,Ac={storage:function(){if(!B.atmosphere.supportStorage()){return 
}var Ad=window.localStorage;
return{init:function(){B(window).on("storage.socket",function(Ae){Ae=Ae.originalEvent;
if(Ae.key===AX&&Ae.newValue){AZ(Ae.newValue)
}})
},signal:function(Ae,Af){Ad.setItem(AX,B.stringifyJSON({target:"c",type:Ae,data:Af}))
},get:function(Ae){return B.parseJSON(Ad.getItem(AX+"-"+Ae))
},set:function(Ae,Af){Ad.setItem(AX+"-"+Ae,B.stringifyJSON(Af))
},close:function(){B(window).off("storage.socket");
Ad.removeItem(AX);
Ad.removeItem(AX+"-opened");
Ad.removeItem(AX+"-children")
}}
},windowref:function(){var Ad=AX.replace(/\W/g,""),Ae=(B('iframe[name="'+Ad+'"]')[0]||B('<iframe name="'+Ad+'" />').hide().appendTo("body")[0]).contentWindow;
return{init:function(){Ae.callbacks=[AZ];
Ae.fire=function(Af){var Ag;
for(Ag=0;
Ag<Ae.callbacks.length;
Ag++){Ae.callbacks[Ag](Af)
}}
},signal:function(Af,Ag){if(!Ae.closed&&Ae.fire){Ae.fire(B.stringifyJSON({target:"c",type:Af,data:Ag}))
}},get:function(Af){return !Ae.closed?Ae[Af]:null
},set:function(Af,Ag){if(!Ae.closed){Ae[Af]=Ag
}},close:function(){}}
}};
function AZ(Ad){var Af=B.parseJSON(Ad),Ae=Af.data;
if(Af.target==="p"){switch(Af.type){case"send":O(Ae);
break;
case"localSend":Y(Ae);
break;
case"close":a();
break
}}}j=function Ab(Ad){AY.signal("message",Ad)
};
function Aa(){document.cookie=AW+"="+encodeURIComponent(B.stringifyJSON({ts:B.now()+1,heir:(AY.get("children")||[])[0]}))+"; path=/"
}AY=Ac.storage()||Ac.windowref();
AY.init();
if(K.logLevel==="debug"){B.atmosphere.debug("Installed StorageService "+AY)
}AY.set("children",[]);
if(AY.get("opened")!=null&&!AY.get("opened")){AY.set("opened",false)
}AW=encodeURIComponent(AX);
Aa();
S=setInterval(Aa,1000);
C=AY
}function o(AZ,Ac,AY){if(K.shared&&Ac!=="local"){AV()
}if(C!=null){C.set("opened",true)
}AY.close=function(){a()
};
if(AO>0&&AZ==="re-connecting"){AY.isReopen=true;
L(AI)
}else{if(AI.error==null){AI.request=AY;
var Aa=AI.state;
AI.state=AZ;
var AX=AI.transport;
AI.transport=Ac;
var Ab=AI.responseBody;
AA();
AI.responseBody=Ab;
AI.state=Aa;
AI.transport=AX
}}}function AR(AZ){AZ.transport="jsonp";
var AY=K;
if((AZ!=null)&&(typeof (AZ)!=="undefined")){AY=AZ
}var AX=AY.url;
if(AY.dispatchUrl!=null){AX+=AY.dispatchUrl
}var Aa=AY.data;
if(AY.attachHeadersAsQueryString){AX=J(AY);
if(Aa!==""){AX+="&X-Atmosphere-Post-Body="+encodeURIComponent(Aa)
}Aa=""
}q=B.ajax({url:AX,type:AY.method,dataType:"jsonp",error:function(Ab,Ad,Ac){AI.error=true;
if(AY.openId){clearTimeout(AY.openId)
}if(AY.heartbeatTimer){clearTimeout(AY.heartbeatTimer)
}if(AY.reconnect&&AO++<AY.maxReconnectOnClose){o("re-connecting",AY.transport,AY);
AE(q,AY,AY.reconnectInterval);
AY.openId=setTimeout(function(){r(AY)
},AY.reconnectInterval+1000)
}else{m(Ab.status,Ac)
}},jsonp:"jsonpTransport",success:function(Ac){if(AY.reconnect){if(AY.maxRequest===-1||AY.requestCount++<AY.maxRequest){Z(q,AY);
E(AY);
if(!AY.executeCallbackBeforeReconnect){AE(q,AY,AY.pollingInterval)
}var Ae=Ac.message;
if(Ae!=null&&typeof Ae!=="string"){try{Ae=B.stringifyJSON(Ae)
}catch(Ad){}}var Ab=M(Ae,AY,AI);
if(!Ab){G(AI.responseBody,"messageReceived",200,AY.transport)
}if(AY.executeCallbackBeforeReconnect){AE(q,AY,AY.pollingInterval)
}}else{B.atmosphere.log(K.logLevel,["JSONP reconnect maximum try reached "+K.requestCount]);
m(0,"maxRequest reached")
}}},data:AY.data,beforeSend:function(Ab){f(Ab,AY,false)
}})
}function AT(Aa){var AY=K;
if((Aa!=null)&&(typeof (Aa)!=="undefined")){AY=Aa
}var AX=AY.url;
if(AY.dispatchUrl!=null){AX+=AY.dispatchUrl
}var Ab=AY.data;
if(AY.attachHeadersAsQueryString){AX=J(AY);
if(Ab!==""){AX+="&X-Atmosphere-Post-Body="+encodeURIComponent(Ab)
}Ab=""
}var AZ=typeof (AY.async)!=="undefined"?AY.async:true;
q=B.ajax({url:AX,type:AY.method,error:function(Ac,Ae,Ad){AI.error=true;
if(Ac.status<300){AE(q,AY)
}else{m(Ac.status,Ad)
}},success:function(Ae,Af,Ad){if(AY.reconnect){if(AY.maxRequest===-1||AY.requestCount++<AY.maxRequest){if(!AY.executeCallbackBeforeReconnect){AE(q,AY,AY.pollingInterval)
}var Ac=M(Ae,AY,AI);
if(!Ac){G(AI.responseBody,"messageReceived",200,AY.transport)
}if(AY.executeCallbackBeforeReconnect){AE(q,AY,AY.pollingInterval)
}}else{B.atmosphere.log(K.logLevel,["AJAX reconnect maximum try reached "+K.requestCount]);
m(0,"maxRequest reached")
}}},beforeSend:function(Ac){f(Ac,AY,false)
},crossDomain:AY.enableXDR,async:AZ})
}function AL(AX){if(K.webSocketImpl!=null){return K.webSocketImpl
}else{if(window.WebSocket){return new WebSocket(AX)
}else{return new MozWebSocket(AX)
}}}function Q(){var AX=J(K);
return decodeURI(B('<a href="'+AX+'"/>')[0].href.replace(/^http/,"ws"))
}function l(){var AX=J(K);
return AX
}function V(AY){AI.transport="sse";
var AX=l(K.url);
if(K.logLevel==="debug"){B.atmosphere.debug("Invoking executeSSE");
B.atmosphere.debug("Using URL: "+AX)
}if(AY&&!K.reconnect){if(y!=null){F()
}return 
}try{y=new EventSource(AX,{withCredentials:K.withCredentials})
}catch(AZ){m(0,AZ);
AP("SSE failed. Downgrading to fallback transport and resending");
return 
}if(K.connectTimeout>0){K.id=setTimeout(function(){if(!AY){F()
}},K.connectTimeout)
}y.onopen=function(Aa){E(K);
if(K.logLevel==="debug"){B.atmosphere.debug("SSE successfully opened")
}if(!K.enableProtocol){if(!AY){o("opening","sse",K)
}else{o("re-opening","sse",K)
}}else{if(K.isReopen){K.isReopen=false;
o("re-opening",K.transport,K)
}}AY=true;
if(K.method==="POST"){AI.state="messageReceived";
y.send(K.data)
}};
y.onmessage=function(Ab){E(K);
if(!K.enableXDR&&Ab.origin!==window.location.protocol+"//"+window.location.host){B.atmosphere.log(K.logLevel,["Origin was not "+window.location.protocol+"//"+window.location.host]);
return 
}AI.state="messageReceived";
AI.status=200;
Ab=Ab.data;
var Aa=M(Ab,K,AI);
if(!Aa){AA();
AI.responseBody="";
AI.messages=[]
}};
y.onerror=function(Aa){clearTimeout(K.id);
if(K.heartbeatTimer){clearTimeout(K.heartbeatTimer)
}if(AI.closedByClientTimeout){return 
}w(AY);
F();
if(AG){B.atmosphere.log(K.logLevel,["SSE closed normally"])
}else{if(!AY){AP("SSE failed. Downgrading to fallback transport and resending")
}else{if(K.reconnect&&(AI.transport==="sse")){if(AO++<K.maxReconnectOnClose){o("re-connecting",K.transport,K);
if(K.reconnectInterval>0){K.reconnectId=setTimeout(function(){V(true)
},K.reconnectInterval)
}else{V(true)
}AI.responseBody="";
AI.messages=[]
}else{B.atmosphere.log(K.logLevel,["SSE reconnect maximum try reached "+AO]);
m(0,"maxReconnectOnClose reached")
}}}}}
}function x(AY){AI.transport="websocket";
var AX=Q(K.url);
if(K.logLevel==="debug"){B.atmosphere.debug("Invoking executeWebSocket");
B.atmosphere.debug("Using URL: "+AX)
}if(AY&&!K.reconnect){if(AM!=null){F()
}return 
}AM=AL(AX);
if(K.webSocketBinaryType!=null){AM.binaryType=K.webSocketBinaryType
}if(K.connectTimeout>0){K.id=setTimeout(function(){if(!AY){var Ab={code:1002,reason:"",wasClean:false};
AM.onclose(Ab);
try{F()
}catch(Ac){}return 
}},K.connectTimeout)
}AM.onopen=function(Ac){E(K);
if(K.logLevel==="debug"){B.atmosphere.debug("Websocket successfully opened")
}var Ab=AY;
if(AM!=null){AM.canSendMessage=true
}if(!K.enableProtocol){AY=true;
if(Ab){o("re-opening","websocket",K)
}else{o("opening","websocket",K)
}}if(AM!=null){if(K.method==="POST"){AI.state="messageReceived";
AM.send(K.data)
}}};
AM.onmessage=function(Ad){E(K);
if(K.enableProtocol){AY=true
}AI.state="messageReceived";
AI.status=200;
Ad=Ad.data;
var Ab=typeof (Ad)==="string";
if(Ab){var Ac=M(Ad,K,AI);
if(!Ac){AA();
AI.responseBody="";
AI.messages=[]
}}else{Ad=N(K,Ad);
if(Ad===""){return 
}AI.responseBody=Ad;
AA();
AI.responseBody=null
}};
AM.onerror=function(Ab){clearTimeout(K.id);
if(K.heartbeatTimer){clearTimeout(K.heartbeatTimer)
}};
AM.onclose=function(Ab){if(AI.state==="closed"){return 
}clearTimeout(K.id);
var Ac=Ab.reason;
if(Ac===""){switch(Ab.code){case 1000:Ac="Normal closure; the connection successfully completed whatever purpose for which it was created.";
break;
case 1001:Ac="The endpoint is going away, either because of a server failure or because the browser is navigating away from the page that opened the connection.";
break;
case 1002:Ac="The endpoint is terminating the connection due to a protocol error.";
break;
case 1003:Ac="The connection is being terminated because the endpoint received data of a type it cannot accept (for example, a text-only endpoint received binary data).";
break;
case 1004:Ac="The endpoint is terminating the connection because a data frame was received that is too large.";
break;
case 1005:Ac="Unknown: no status code was provided even though one was expected.";
break;
case 1006:Ac="Connection was closed abnormally (that is, with no close frame being sent).";
break
}}if(K.logLevel==="warn"){B.atmosphere.warn("Websocket closed, reason: "+Ac);
B.atmosphere.warn("Websocket closed, wasClean: "+Ab.wasClean)
}if(AI.closedByClientTimeout){return 
}w(AY);
AI.state="closed";
if(AG){B.atmosphere.log(K.logLevel,["Websocket closed normally"])
}else{if(!AY){AP("Websocket failed. Downgrading to Comet and resending")
}else{if(K.reconnect&&AI.transport==="websocket"&&Ab.code!==1001){F();
if(AO++<K.maxReconnectOnClose){o("re-connecting",K.transport,K);
if(K.reconnectInterval>0){K.reconnectId=setTimeout(function(){AI.responseBody="";
AI.messages=[];
x(true)
},K.reconnectInterval)
}else{AI.responseBody="";
AI.messages=[];
x(true)
}}else{B.atmosphere.log(K.logLevel,["Websocket reconnect maximum try reached "+K.requestCount]);
if(K.logLevel==="warn"){B.atmosphere.warn("Websocket error, reason: "+Ab.reason)
}m(0,"maxReconnectOnClose reached")
}}}}};
var AZ=navigator.userAgent.toLowerCase();
var Aa=AZ.indexOf("android")>-1;
if(Aa&&AM.url===undefined){AM.onclose({reason:"Android 4.1 does not support websockets.",wasClean:false})
}}function N(Ad,Ac){var Ab=Ac;
if(Ad.transport==="polling"){return Ab
}if(B.trim(Ac).length!==0&&Ad.enableProtocol&&Ad.firstMessage){var Ae=Ad.trackMessageLength?1:0;
var Aa=Ac.split(Ad.messageDelimiter);
if(Aa.length<=Ae+1){return Ab
}Ad.firstMessage=false;
Ad.uuid=B.trim(Aa[Ae]);
if(Aa.length<=Ae+2){B.atmosphere.log("error",["Protocol data not sent by the server. If you enable protocol on client side, be sure to install JavascriptProtocol interceptor on server side.Also note that atmosphere-runtime 2.2+ should be used."])
}var AX=parseInt(B.trim(Aa[Ae+1]),10);
AC=Aa[Ae+2];
if(!isNaN(AX)&&AX>0){var AZ=function(){O(AC);
Ad.heartbeatTimer=setTimeout(AZ,AX)
};
Ad.heartbeatTimer=setTimeout(AZ,AX)
}b=false;
if(Ad.transport!=="long-polling"){r(Ad)
}B.atmosphere.uuid=Ad.uuid;
Ab="";
Ae=Ad.trackMessageLength?4:3;
if(Aa.length>Ae+1){for(var AY=Ae;
AY<Aa.length;
AY++){Ab+=Aa[AY];
if(AY+1!==Aa.length){Ab+=Ad.messageDelimiter
}}}if(Ad.ackInterval!==0){setTimeout(function(){O("...ACK...")
},Ad.ackInterval)
}}else{if(Ad.enableProtocol&&Ad.firstMessage&&B.browser.msie&&+B.browser.version.split(".")[0]<10){B.atmosphere.log(K.logLevel,["Receiving unexpected data from IE"])
}else{r(Ad)
}}return Ab
}function E(AX){AX.timedOut=false;
clearTimeout(AX.id);
if(AX.timeout>0&&AX.transport!=="polling"){AX.id=setTimeout(function(){AX.timedOut=true;
AS(AX);
W();
F()
},AX.timeout)
}}function AS(AX){AI.closedByClientTimeout=true;
AI.state="closedByClient";
AI.responseBody="";
AI.status=408;
AI.messages=[];
AA()
}function m(AX,AY){F();
clearTimeout(K.id);
AI.state="error";
AI.reasonPhrase=AY;
AI.responseBody="";
AI.status=AX;
AI.messages=[];
AA()
}function M(Ab,Aa,AX){Ab=N(Aa,Ab);
if(Ab.length===0){return true
}AX.responseBody=Ab;
if(Aa.trackMessageLength){Ab=AX.partialMessage+Ab;
var AZ=[];
var AY=Ab.indexOf(Aa.messageDelimiter);
while(AY!==-1){var Ad=Ab.substring(0,AY);
var Ac=parseInt(Ad,10);
if(isNaN(Ac)){throw'message length "'+Ad+'" is not a number'
}AY+=Aa.messageDelimiter.length;
if(AY+Ac>Ab.length){AY=-1
}else{AZ.push(Ab.substring(AY,AY+Ac));
Ab=Ab.substring(AY+Ac,Ab.length);
AY=Ab.indexOf(Aa.messageDelimiter)
}}AX.partialMessage=Ab;
if(AZ.length!==0){AX.responseBody=AZ.join(Aa.messageDelimiter);
AX.messages=AZ;
return false
}else{AX.responseBody="";
AX.messages=[];
return true
}}else{AX.responseBody=Ab
}return false
}function AP(AX){B.atmosphere.log(K.logLevel,[AX]);
if(typeof (K.onTransportFailure)!=="undefined"){K.onTransportFailure(AX,K)
}else{if(typeof (B.atmosphere.onTransportFailure)!=="undefined"){B.atmosphere.onTransportFailure(AX,K)
}}K.transport=K.fallbackTransport;
var AY=K.connectTimeout===-1?0:K.connectTimeout;
if(K.reconnect&&K.transport!=="none"||K.transport==null){K.method=K.fallbackMethod;
AI.transport=K.fallbackTransport;
K.fallbackTransport="none";
if(AY>0){K.reconnectId=setTimeout(function(){s()
},AY)
}else{s()
}}else{m(500,"Unable to reconnect with fallback transport")
}}function J(AZ,AX){var AY=K;
if((AZ!=null)&&(typeof (AZ)!=="undefined")){AY=AZ
}if(AX==null){AX=AY.url
}if(!AY.attachHeadersAsQueryString){return AX
}if(AX.indexOf("X-Atmosphere-Framework")!==-1){return AX
}AX+=(AX.indexOf("?")!==-1)?"&":"?";
AX+="X-Atmosphere-tracking-id="+AY.uuid;
AX+="&X-Atmosphere-Framework="+B.atmosphere.version;
AX+="&X-Atmosphere-Transport="+AY.transport;
if(AY.trackMessageLength){AX+="&X-Atmosphere-TrackMessageSize=true"
}if(AY.heartbeat!==null&&AY.heartbeat.server!==null){AX+="&X-Heartbeat-Server="+AY.heartbeat.server
}if(AY.contentType!==""){AX+="&Content-Type="+(AY.transport==="websocket"?AY.contentType:encodeURIComponent(AY.contentType))
}if(AY.enableProtocol){AX+="&X-atmo-protocol=true"
}B.each(AY.headers,function(Aa,Ac){var Ab=B.isFunction(Ac)?Ac.call(this,AY,AZ,AI):Ac;
if(Ab!=null){AX+="&"+encodeURIComponent(Aa)+"="+encodeURIComponent(Ab)
}});
return AX
}function r(AX){if(!AX.isOpen){AX.isOpen=true;
o("opening",AX.transport,AX)
}else{if(AX.isReopen){AX.isReopen=false;
o("re-opening",AX.transport,AX)
}}}function e(Aa){var AY=K;
if((Aa!=null)||(typeof (Aa)!=="undefined")){AY=Aa
}AY.lastIndex=0;
AY.readyState=0;
if((AY.transport==="jsonp")||((AY.enableXDR)&&(B.atmosphere.checkCORSSupport()))){AR(AY);
return 
}if(AY.transport==="ajax"){AT(Aa);
return 
}if(B.browser.msie&&+B.browser.version.split(".")[0]<10){if((AY.transport==="streaming")){if(AY.enableXDR&&window.XDomainRequest){i(AY)
}else{AQ(AY)
}return 
}if((AY.enableXDR)&&(window.XDomainRequest)){i(AY);
return 
}}var Ab=function(){AY.lastIndex=0;
if(AY.reconnect&&AO++<AY.maxReconnectOnClose){AI.ffTryingReconnect=true;
o("re-connecting",Aa.transport,Aa);
AE(AZ,AY,Aa.reconnectInterval)
}else{m(0,"maxReconnectOnClose reached")
}};
var AX=function(){AI.errorHandled=true;
F();
Ab()
};
if(AY.reconnect&&(AY.maxRequest===-1||AY.requestCount++<AY.maxRequest)){var AZ=B.ajaxSettings.xhr();
AZ.hasData=false;
f(AZ,AY,true);
if(AY.suspend){T=AZ
}if(AY.transport!=="polling"){AI.transport=AY.transport;
AZ.onabort=function(){w(true)
};
AZ.onerror=function(){AI.error=true;
AI.ffTryingReconnect=true;
try{AI.status=XMLHttpRequest.status
}catch(Ac){AI.status=500
}if(!AI.status){AI.status=500
}if(!AI.errorHandled){F();
Ab()
}}
}AZ.onreadystatechange=function(){if(AG){return 
}AI.error=null;
var Ad=false;
var Aj=false;
if(AY.transport==="streaming"&&AY.readyState>2&&AZ.readyState===4){F();
Ab();
return 
}AY.readyState=AZ.readyState;
if(AY.transport==="streaming"&&AZ.readyState>=3){Aj=true
}else{if(AY.transport==="long-polling"&&AZ.readyState===4){Aj=true
}}E(K);
if(AY.transport!=="polling"){var Ac=200;
if(AZ.readyState===4){Ac=AZ.status>1000?0:AZ.status
}if(!AY.reconnectOnServerError&&(Ac>=300&&Ac<600)){m(Ac,AZ.statusText);
return 
}if(Ac>=300||Ac===0){AX();
return 
}if((!AY.enableProtocol||!Aa.firstMessage)&&AZ.readyState===2){if(B.browser.mozilla&&AI.ffTryingReconnect){AI.ffTryingReconnect=false;
setTimeout(function(){if(!AI.ffTryingReconnect){r(AY)
}},500)
}else{r(AY)
}}}else{if(AZ.readyState===4){Aj=true
}}if(Aj){var Ag=AZ.responseText;
if(B.trim(Ag).length===0&&AY.transport==="long-polling"){if(!AZ.hasData){AE(AZ,AY,AY.pollingInterval)
}else{AZ.hasData=false
}return 
}AZ.hasData=true;
Z(AZ,K);
if(AY.transport==="streaming"){if(!B.browser.opera){var Af=Ag.substring(AY.lastIndex,Ag.length);
Ad=M(Af,AY,AI);
AY.lastIndex=Ag.length;
if(Ad){return 
}}else{B.atmosphere.iterate(function(){if(AI.status!==500&&AZ.responseText.length>AY.lastIndex){try{AI.status=AZ.status;
AI.headers=A(AZ.getAllResponseHeaders());
Z(AZ,K)
}catch(Al){AI.status=404
}E(K);
AI.state="messageReceived";
var Ak=AZ.responseText.substring(AY.lastIndex);
AY.lastIndex=AZ.responseText.length;
Ad=M(Ak,AY,AI);
if(!Ad){AA()
}if(c(AZ,AY)){d(AZ,AY);
return 
}}else{if(AI.status>400){AY.lastIndex=AZ.responseText.length;
return false
}}},0)
}}else{Ad=M(Ag,AY,AI)
}var Ai=c(AZ,AY);
try{AI.status=AZ.status;
AI.headers=A(AZ.getAllResponseHeaders());
Z(AZ,AY)
}catch(Ah){AI.status=404
}if(AY.suspend){AI.state=AI.status===0?"closed":"messageReceived"
}else{AI.state="messagePublished"
}var Ae=!Ai&&Aa.transport!=="streaming"&&Aa.transport!=="polling";
if(Ae&&!AY.executeCallbackBeforeReconnect){AE(AZ,AY,AY.pollingInterval)
}if(AI.responseBody.length!==0&&!Ad){AA()
}if(Ae&&AY.executeCallbackBeforeReconnect){AE(AZ,AY,AY.pollingInterval)
}if(Ai){d(AZ,AY)
}}};
AZ.send(AY.data);
P=true
}else{if(AY.logLevel==="debug"){B.atmosphere.log(AY.logLevel,["Max re-connection reached."])
}m(0,"maxRequest reached")
}}function d(AY,AX){a();
AG=false;
AE(AY,AX,500)
}function f(AZ,Aa,AY){var AX=Aa.url;
if(Aa.dispatchUrl!=null&&Aa.method==="POST"){AX+=Aa.dispatchUrl
}AX=J(Aa,AX);
AX=B.atmosphere.prepareURL(AX);
if(AY){AZ.open(Aa.method,AX,true);
if(Aa.connectTimeout>0){Aa.id=setTimeout(function(){if(Aa.requestCount===0){F();
G("Connect timeout","closed",200,Aa.transport)
}},Aa.connectTimeout)
}}if(K.withCredentials&&K.transport!=="websocket"){if("withCredentials" in AZ){AZ.withCredentials=true
}}if(!K.dropHeaders){AZ.setRequestHeader("X-Atmosphere-Framework",B.atmosphere.version);
AZ.setRequestHeader("X-Atmosphere-Transport",Aa.transport);
if(AZ.heartbeat!==null&&AZ.heartbeat.server!==null){AZ.setRequestHeader("X-Heartbeat-Server",AZ.heartbeat.server)
}if(Aa.trackMessageLength){AZ.setRequestHeader("X-Atmosphere-TrackMessageSize","true")
}AZ.setRequestHeader("X-Atmosphere-tracking-id",Aa.uuid);
B.each(Aa.headers,function(Ab,Ad){var Ac=B.isFunction(Ad)?Ad.call(this,AZ,Aa,AY,AI):Ad;
if(Ac!=null){AZ.setRequestHeader(Ab,Ac)
}})
}if(Aa.contentType!==""){AZ.setRequestHeader("Content-Type",Aa.contentType)
}}function AE(AY,AZ,Aa){if(AZ.reconnect||(AZ.suspend&&P)){var AX=0;
if(AY.readyState>1){AX=AY.status>1000?0:AY.status
}AI.status=AX===0?204:AX;
AI.reason=AX===0?"Server resumed the connection or down.":"OK";
clearTimeout(AZ.id);
if(AZ.reconnectId){clearTimeout(AZ.reconnectId);
delete AZ.reconnectId
}if(Aa>0){setTimeout(function(){K.reconnectId=e(AZ)
},Aa)
}else{e(AZ)
}}}function L(AX){AX.state="re-connecting";
AD(AX)
}function i(AX){if(AX.transport!=="polling"){I=u(AX);
I.open()
}else{u(AX).open()
}}function u(AZ){var AY=K;
if((AZ!=null)&&(typeof (AZ)!=="undefined")){AY=AZ
}var Ae=AY.transport;
var Ad=0;
var AX=new window.XDomainRequest();
var Ab=function(){if(AY.transport==="long-polling"&&(AY.reconnect&&(AY.maxRequest===-1||AY.requestCount++<AY.maxRequest))){AX.status=200;
o("re-connecting",AZ.transport,AZ);
i(AY)
}};
var Ac=AY.rewriteURL||function(Ag){var Af=/(?:^|;\s*)(JSESSIONID|PHPSESSID)=([^;]*)/.exec(document.cookie);
switch(Af&&Af[1]){case"JSESSIONID":return Ag.replace(/;jsessionid=[^\?]*|(\?)|$/,";jsessionid="+Af[2]+"$1");
case"PHPSESSID":return Ag.replace(/\?PHPSESSID=[^&]*&?|\?|$/,"?PHPSESSID="+Af[2]+"&").replace(/&$/,"")
}return Ag
};
AX.onprogress=function(){Aa(AX)
};
AX.onerror=function(){if(AY.transport!=="polling"){F();
if(AO++<AY.maxReconnectOnClose){if(AY.reconnectInterval>0){AY.reconnectId=setTimeout(function(){o("re-connecting",AZ.transport,AZ);
i(AY)
},AY.reconnectInterval)
}else{o("re-connecting",AZ.transport,AZ);
i(AY)
}}else{m(0,"maxReconnectOnClose reached")
}}};
AX.onload=function(){if(K.timedOut){K.timedOut=false;
F();
AY.lastIndex=0;
if(AY.reconnect&&AO++<AY.maxReconnectOnClose){o("re-connecting",AZ.transport,AZ);
Ab()
}else{m(0,"maxReconnectOnClose reached")
}}};
var Aa=function(Af){clearTimeout(AY.id);
var Ah=Af.responseText;
Ah=Ah.substring(Ad);
Ad+=Ah.length;
if(Ae!=="polling"){E(AY);
var Ag=M(Ah,AY,AI);
if(Ae==="long-polling"&&B.trim(Ah).length===0){return 
}if(AY.executeCallbackBeforeReconnect){Ab()
}if(!Ag){G(AI.responseBody,"messageReceived",200,Ae)
}if(!AY.executeCallbackBeforeReconnect){Ab()
}}};
return{open:function(){var Af=AY.url;
if(AY.dispatchUrl!=null){Af+=AY.dispatchUrl
}Af=J(AY,Af);
AX.open(AY.method,Ac(Af));
if(AY.method==="GET"){AX.send()
}else{AX.send(AY.data)
}if(AY.connectTimeout>0){AY.id=setTimeout(function(){if(AY.requestCount===0){F();
G("Connect timeout","closed",200,AY.transport)
}},AY.connectTimeout)
}},close:function(){AX.abort()
}}
}function AQ(AX){I=t(AX);
I.open()
}function t(Aa){var AZ=K;
if((Aa!=null)&&(typeof (Aa)!=="undefined")){AZ=Aa
}var AY;
var Ab=new window.ActiveXObject("htmlfile");
Ab.open();
Ab.close();
var AX=AZ.url;
if(AZ.dispatchUrl!=null){AX+=AZ.dispatchUrl
}if(AZ.transport!=="polling"){AI.transport=AZ.transport
}return{open:function(){var Ac=Ab.createElement("iframe");
AX=J(AZ);
if(AZ.data!==""){AX+="&X-Atmosphere-Post-Body="+encodeURIComponent(AZ.data)
}AX=B.atmosphere.prepareURL(AX);
Ac.src=AX;
Ab.body.appendChild(Ac);
var Ad=Ac.contentDocument||Ac.contentWindow.document;
AY=B.atmosphere.iterate(function(){try{if(!Ad.firstChild){return 
}if(Ad.readyState==="complete"){try{B.noop(Ad.fileSize)
}catch(Aj){G("Connection Failure","error",500,AZ.transport);
return false
}}var Ag=Ad.body?Ad.body.lastChild:Ad;
var Ai=function(){var Al=Ag.cloneNode(true);
Al.appendChild(Ad.createTextNode("."));
var Ak=Al.innerText;
Ak=Ak.substring(0,Ak.length-1);
return Ak
};
if(!B.nodeName(Ag,"pre")){var Af=Ad.head||Ad.getElementsByTagName("head")[0]||Ad.documentElement||Ad;
var Ae=Ad.createElement("script");
Ae.text="document.write('<plaintext>')";
Af.insertBefore(Ae,Af.firstChild);
Af.removeChild(Ae);
Ag=Ad.body.lastChild
}if(AZ.closed){AZ.isReopen=true
}AY=B.atmosphere.iterate(function(){var Al=Ai();
if(Al.length>AZ.lastIndex){E(K);
AI.status=200;
AI.error=null;
Ag.innerText="";
var Ak=M(Al,AZ,AI);
if(Ak){return""
}G(AI.responseBody,"messageReceived",200,AZ.transport)
}AZ.lastIndex=0;
if(Ad.readyState==="complete"){w(true);
o("re-connecting",AZ.transport,AZ);
if(AZ.reconnectInterval>0){AZ.reconnectId=setTimeout(function(){AQ(AZ)
},AZ.reconnectInterval)
}else{AQ(AZ)
}return false
}},null);
return false
}catch(Ah){AI.error=true;
o("re-connecting",AZ.transport,AZ);
if(AO++<AZ.maxReconnectOnClose){if(AZ.reconnectInterval>0){AZ.reconnectId=setTimeout(function(){AQ(AZ)
},AZ.reconnectInterval)
}else{AQ(AZ)
}}else{m(0,"maxReconnectOnClose reached")
}Ab.execCommand("Stop");
Ab.close();
return false
}})
},close:function(){if(AY){AY()
}Ab.execCommand("Stop");
w(true)
}}
}function O(AX){if(AN!=null){X(AX)
}else{if(T!=null||y!=null){h(AX)
}else{if(I!=null){D(AX)
}else{if(q!=null){U(AX)
}else{if(AM!=null){p(AX)
}else{m(0,"No suspended connection available");
B.atmosphere.error("No suspended connection available. Make sure atmosphere.subscribe has been called and request.onOpen invoked before invoking this method")
}}}}}}function X(AX){AN.send(AX)
}function AH(AY){if(AY.length===0){return 
}try{if(AN){AN.localSend(AY)
}else{if(C){C.signal("localMessage",B.stringifyJSON({id:k,event:AY}))
}}}catch(AX){B.atmosphere.error(AX)
}}function h(AY){var AX=R(AY);
e(AX)
}function D(AY){if(K.enableXDR&&B.atmosphere.checkCORSSupport()){var AX=R(AY);
AX.reconnect=false;
AR(AX)
}else{h(AY)
}}function U(AX){h(AX)
}function g(AX){var AY=AX;
if(typeof (AY)==="object"){AY=AX.data
}return AY
}function R(AY){var AZ=g(AY);
var AX={connected:false,timeout:60000,method:"POST",url:K.url,contentType:K.contentType,headers:K.headers,reconnect:true,callback:null,data:AZ,suspend:false,maxRequest:-1,logLevel:"info",requestCount:0,withCredentials:K.withCredentials,transport:"polling",isOpen:true,attachHeadersAsQueryString:true,enableXDR:K.enableXDR,uuid:K.uuid,dispatchUrl:K.dispatchUrl,enableProtocol:false,messageDelimiter:"|",trackMessageLength:K.trackMessageLength,maxReconnectOnClose:K.maxReconnectOnClose,heartbeatTimer:K.heartbeatTimer,heartbeat:K.heartbeat};
if(typeof (AY)==="object"){AX=B.extend(AX,AY)
}return AX
}function p(AX){var Aa=B.atmosphere.isBinary(AX)?AX:g(AX);
var AY;
try{if(K.dispatchUrl!=null){AY=K.webSocketPathDelimiter+K.dispatchUrl+K.webSocketPathDelimiter+Aa
}else{AY=Aa
}if(!AM.canSendMessage){B.atmosphere.error("WebSocket not connected.");
return 
}AM.send(AY)
}catch(AZ){AM.onclose=function(Ab){};
F();
AP("Websocket failed. Downgrading to Comet and resending "+AX);
h(AX)
}}function Y(AY){var AX=B.parseJSON(AY);
if(AX.id!==k){if(typeof (K.onLocalMessage)!=="undefined"){K.onLocalMessage(AX.event)
}else{if(typeof (B.atmosphere.onLocalMessage)!=="undefined"){B.atmosphere.onLocalMessage(AX.event)
}}}}function G(Aa,AX,AY,AZ){AI.responseBody=Aa;
AI.transport=AZ;
AI.status=AY;
AI.state=AX;
AA()
}function Z(AX,AZ){if(!AZ.readResponsesHeaders){if(!AZ.enableProtocol){AZ.uuid=B.atmosphere.guid()
}}else{try{var AY=AX.getResponseHeader("X-Atmosphere-tracking-id");
if(AY&&AY!=null){AZ.uuid=AY.split(" ").pop()
}}catch(Aa){}}}function AD(AX){H(AX,K);
H(AX,B.atmosphere)
}function H(AY,AZ){switch(AY.state){case"messageReceived":AO=0;
if(typeof (AZ.onMessage)!=="undefined"){AZ.onMessage(AY)
}break;
case"error":if(typeof (AZ.onError)!=="undefined"){AZ.onError(AY)
}break;
case"opening":delete K.closed;
if(typeof (AZ.onOpen)!=="undefined"){AZ.onOpen(AY)
}break;
case"messagePublished":if(typeof (AZ.onMessagePublished)!=="undefined"){AZ.onMessagePublished(AY)
}break;
case"re-connecting":if(typeof (AZ.onReconnect)!=="undefined"){AZ.onReconnect(K,AY)
}break;
case"closedByClient":if(typeof (AZ.onClientTimeout)!=="undefined"){AZ.onClientTimeout(K)
}break;
case"re-opening":delete K.closed;
if(typeof (AZ.onReopen)!=="undefined"){AZ.onReopen(K,AY)
}break;
case"fail-to-reconnect":if(typeof (AZ.onFailureToReconnect)!=="undefined"){AZ.onFailureToReconnect(K,AY)
}break;
case"unsubscribe":case"closed":var AX=typeof (K.closed)!=="undefined"?K.closed:false;
if(!AX){if(typeof (AZ.onClose)!=="undefined"){AZ.onClose(AY)
}}K.closed=true;
break
}}function w(AX){if(AI.state!=="closed"){AI.state="closed";
AI.responseBody="";
AI.messages=[];
AI.status=!AX?501:200;
AA()
}}function AA(){var AZ=function(Ac,Ad){Ad(AI)
};
if(AN==null&&j!=null){j(AI.responseBody)
}K.reconnect=K.mrequest;
var AX=typeof (AI.responseBody)==="string";
var Aa=(AX&&K.trackMessageLength)?(AI.messages.length>0?AI.messages:[""]):new Array(AI.responseBody);
for(var AY=0;
AY<Aa.length;
AY++){if(Aa.length>1&&Aa[AY].length===0){continue
}AI.responseBody=(AX)?B.trim(Aa[AY]):Aa[AY];
if(AN==null&&j!=null){j(AI.responseBody)
}if((AI.responseBody.length===0||(AX&&AC===AI.responseBody))&&AI.state==="messageReceived"){continue
}AD(AI);
if(B.atmosphere.callbacks.length>0){if(K.logLevel==="debug"){B.atmosphere.debug("Invoking "+B.atmosphere.callbacks.length+" global callbacks: "+AI.state)
}try{B.each(B.atmosphere.callbacks,AZ)
}catch(Ab){B.atmosphere.log(K.logLevel,["Callback exception"+Ab])
}}if(typeof (K.callback)==="function"){if(K.logLevel==="debug"){B.atmosphere.debug("Invoking request callbacks")
}try{K.callback(AI)
}catch(Ab){B.atmosphere.log(K.logLevel,["Callback exception"+Ab])
}}}}function c(AY,AX){if(AI.partialMessage===""&&(AX.transport==="streaming")&&(AY.responseText.length>AX.maxStreamingLength)){return true
}return false
}function W(){if(K.enableProtocol&&!K.firstMessage){var AY="X-Atmosphere-Transport=close&X-Atmosphere-tracking-id="+K.uuid;
B.each(K.headers,function(AZ,Ab){var Aa=B.isFunction(Ab)?Ab.call(this,K,K,AI):Ab;
if(Aa!=null){AY+="&"+encodeURIComponent(AZ)+"="+encodeURIComponent(Aa)
}});
var AX=K.url.replace(/([?&])_=[^&]*/,AY);
AX=AX+(AX===K.url?(/\?/.test(K.url)?"&":"?")+AY:"");
if(K.connectTimeout>0){B.ajax({url:AX,async:K.closeAsync,timeout:K.connectTimeout,cache:false,crossDomain:K.enableXDR})
}else{B.ajax({url:AX,async:K.closeAsync,cache:false,crossDomain:K.enableXDR})
}}}function a(){if(K.reconnectId){clearTimeout(K.reconnectId);
delete K.reconnectId
}if(K.heartbeatTimer){clearTimeout(K.heartbeatTimer)
}K.reconnect=false;
AG=true;
AI.request=K;
AI.state="unsubscribe";
AI.responseBody="";
AI.status=408;
AA();
W();
F()
}function F(){AI.partialMessage="";
if(K.id){clearTimeout(K.id)
}if(K.heartbeatTimer){clearTimeout(K.heartbeatTimer)
}if(I!=null){I.close();
I=null
}if(q!=null){q.abort();
q=null
}if(T!=null){T.abort();
T=null
}if(AM!=null){if(AM.canSendMessage){AM.close()
}AM=null
}if(y!=null){y.close();
y=null
}z()
}function z(){if(C!=null){clearInterval(S);
document.cookie=AW+"=; expires=Thu, 01 Jan 1970 00:00:00 GMT; path=/";
C.signal("close",{reason:"",heir:!AG?k:(C.get("children")||[])[0]});
C.close()
}if(AN!=null){AN.close()
}}this.subscribe=function(AX){AF(AX);
s()
};
this.execute=function(){s()
};
this.invokeCallback=function(){AA()
};
this.close=function(){a()
};
this.disconnect=function(){W()
};
this.getUrl=function(){return K.url
};
this.push=function(AZ,AY){if(AY!=null){var AX=K.dispatchUrl;
K.dispatchUrl=AY;
O(AZ);
K.dispatchUrl=AX
}else{O(AZ)
}};
this.getUUID=function(){return K.uuid
};
this.pushLocal=function(AX){AH(AX)
};
this.enableProtocol=function(AX){return K.enableProtocol
};
this.request=K;
this.response=AI
},subscribe:function(C,F,E){if(typeof (F)==="function"){B.atmosphere.addCallback(F)
}if(typeof (C)!=="string"){E=C
}else{E.url=C
}B.atmosphere.uuid=((typeof (E)!=="undefined")&&typeof (E.uuid)!=="undefined")?E.uuid:0;
var D=new B.atmosphere.AtmosphereRequest(E);
D.execute();
B.atmosphere.requests[B.atmosphere.requests.length]=D;
return D
},addCallback:function(C){if(B.inArray(C,B.atmosphere.callbacks)===-1){B.atmosphere.callbacks.push(C)
}},removeCallback:function(D){var C=B.inArray(D,B.atmosphere.callbacks);
if(C!==-1){B.atmosphere.callbacks.splice(C,1)
}},unsubscribe:function(){if(B.atmosphere.requests.length>0){var C=[].concat(B.atmosphere.requests);
for(var E=0;
E<C.length;
E++){var D=C[E];
D.close();
clearTimeout(D.response.request.id);
if(D.heartbeatTimer){clearTimeout(D.heartbeatTimer)
}}}B.atmosphere.requests=[];
B.atmosphere.callbacks=[]
},unsubscribeUrl:function(D){var C=-1;
if(B.atmosphere.requests.length>0){for(var F=0;
F<B.atmosphere.requests.length;
F++){var E=B.atmosphere.requests[F];
if(E.getUrl()===D){E.close();
clearTimeout(E.response.request.id);
if(E.heartbeatTimer){clearTimeout(E.heartbeatTimer)
}C=F;
break
}}}if(C>=0){B.atmosphere.requests.splice(C,1)
}},publish:function(D){if(typeof (D.callback)==="function"){B.atmosphere.addCallback(D.callback)
}D.transport="polling";
var C=new B.atmosphere.AtmosphereRequest(D);
B.atmosphere.requests[B.atmosphere.requests.length]=C;
return C
},checkCORSSupport:function(){if(B.browser.msie&&!window.XDomainRequest&&+B.browser.version.split(".")[0]<11){return true
}else{if(B.browser.opera&&+B.browser.version.split(".")[0]<12){return true
}else{if(B.trim(navigator.userAgent).slice(0,16)==="KreaTVWebKit/531"){return true
}else{if(B.trim(navigator.userAgent).slice(-7).toLowerCase()==="kreatel"){return true
}}}}var C=navigator.userAgent.toLowerCase();
var D=C.indexOf("android")>-1;
if(D){return true
}return false
},S4:function(){return(((1+Math.random())*65536)|0).toString(16).substring(1)
},guid:function(){return(B.atmosphere.S4()+B.atmosphere.S4()+"-"+B.atmosphere.S4()+"-"+B.atmosphere.S4()+"-"+B.atmosphere.S4()+"-"+B.atmosphere.S4()+B.atmosphere.S4()+B.atmosphere.S4())
},prepareURL:function(D){var E=B.now();
var C=D.replace(/([?&])_=[^&]*/,"$1_="+E);
return C+(C===D?(/\?/.test(D)?"&":"?")+"_="+E:"")
},param:function(C){return B.param(C,B.ajaxSettings.traditional)
},supportStorage:function(){var D=window.localStorage;
if(D){try{D.setItem("t","t");
D.removeItem("t");
return window.StorageEvent&&!B.browser.msie&&!(B.browser.mozilla&&B.browser.version.split(".")[0]==="1")
}catch(C){}}return false
},iterate:function(E,D){var F;
D=D||0;
(function C(){F=setTimeout(function(){if(E()===false){return 
}C()
},D)
})();
return function(){clearTimeout(F)
}
},log:function(E,D){if(window.console){var C=window.console[E];
if(typeof C==="function"){C.apply(window.console,D)
}}},warn:function(){B.atmosphere.log("warn",arguments)
},info:function(){B.atmosphere.log("info",arguments)
},debug:function(){B.atmosphere.log("debug",arguments)
},error:function(){B.atmosphere.log("error",arguments)
},isBinary:function(C){return/^\[object\s(?:Blob|ArrayBuffer|.+Array)\]$/.test(Object.prototype.toString.call(C))
}};
(function(){var C,D;
B.uaMatch=function(F){F=F.toLowerCase();
var E=/(chrome)[ \/]([\w.]+)/.exec(F)||/(webkit)[ \/]([\w.]+)/.exec(F)||/(opera)(?:.*version|)[ \/]([\w.]+)/.exec(F)||/(msie) ([\w.]+)/.exec(F)||/(trident)(?:.*? rv:([\w.]+)|)/.exec(F)||F.indexOf("compatible")<0&&/(mozilla)(?:.*? rv:([\w.]+)|)/.exec(F)||[];
return{browser:E[1]||"",version:E[2]||"0"}
};
C=B.uaMatch(navigator.userAgent);
D={};
if(C.browser){D[C.browser]=true;
D.version=C.version
}if(D.chrome){D.webkit=true
}else{if(D.webkit){D.safari=true
}}if(D.trident){D.msie=true
}B.browser=D;
B.sub=function(){function E(H,I){return new E.fn.init(H,I)
}B.extend(true,E,this);
E.superclass=this;
E.fn=E.prototype=this();
E.fn.constructor=E;
E.sub=this.sub;
E.fn.init=function G(H,I){if(I&&I instanceof B&&!(I instanceof E)){I=E(I)
}return B.fn.init.call(this,H,I,F)
};
E.fn.init.prototype=E.fn;
var F=E(document);
return E
}
})();
(function(F){var H=/[\\\"\x00-\x1f\x7f-\x9f\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g,E={"\b":"\\b","\t":"\\t","\n":"\\n","\f":"\\f","\r":"\\r",'"':'\\"',"\\":"\\\\"};
function C(I){return'"'+I.replace(H,function(J){var K=E[J];
return typeof K==="string"?K:"\\u"+("0000"+J.charCodeAt(0).toString(16)).slice(-4)
})+'"'
}function D(I){return I<10?"0"+I:I
}function G(N,M){var L,K,I,J,P=M[N],O=typeof P;
if(P&&typeof P==="object"&&typeof P.toJSON==="function"){P=P.toJSON(N);
O=typeof P
}switch(O){case"string":return C(P);
case"number":return isFinite(P)?String(P):"null";
case"boolean":return String(P);
case"object":if(!P){return"null"
}switch(Object.prototype.toString.call(P)){case"[object Date]":return isFinite(P.valueOf())?'"'+P.getUTCFullYear()+"-"+D(P.getUTCMonth()+1)+"-"+D(P.getUTCDate())+"T"+D(P.getUTCHours())+":"+D(P.getUTCMinutes())+":"+D(P.getUTCSeconds())+'Z"':"null";
case"[object Array]":I=P.length;
J=[];
for(L=0;
L<I;
L++){J.push(G(L,P)||"null")
}return"["+J.join(",")+"]";
default:J=[];
for(L in P){if(Object.prototype.hasOwnProperty.call(P,L)){K=G(L,P);
if(K){J.push(C(L)+":"+K)
}}}return"{"+J.join(",")+"}"
}}}F.stringifyJSON=function(I){if(window.JSON&&window.JSON.stringify){return window.JSON.stringify(I)
}return G("",{"":I})
}
}(B))
}));;(function(B,A){A.ui=A.ui||{};
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
})(RichFaces.jQuery,RichFaces);;(function(E,C){C.ui=C.ui||{};
var B={useNative:false};
C.ui.Focus=C.BaseComponent.extendClass({name:"Focus",init:function(J,I){F.constructor.call(this,J);
I=this.options=E.extend({},B,I);
this.attachToDom(this.id);
var L=E(document.getElementById(J+"InputFocus"));
var K=this.options.focusCandidates;
E(document).on("focus",":tabbable",function(O){var N=E(O.target);
if(!N.is(":editable")){return 
}var M=O.target.id||"";
N.parents().each(function(){var P=E(this).attr("id");
if(P){M+=" "+P
}});
L.val(M);
C.log.debug("Focus - clientId candidates for components: "+M)
});
if(this.options.mode==="VIEW"){E(document).on("ajaxsubmit submit","form",function(O){var N=E(O.target);
var M=E("input[name='org.richfaces.focus']",N);
if(!M.length){M=E('<input name="org.richfaces.focus" type="hidden" />').appendTo(N)
}M.val(L.val())
})
}this.options.applyFocus=E.proxy(function(){var M=E();
if(K){var N=K;
C.log.debug("Focus - focus candidates: "+N);
N=N.split(" ");
E.each(N,function(P,O){var Q=E(document.getElementById(O));
M=M.add(E(":tabbable",Q));
if(Q.is(":tabbable")){M=M.add(Q)
}});
if(M.length==0){M=E("form").has(L).find(":tabbable")
}}else{if(this.options.mode=="VIEW"){M=E("body form:first :tabbable")
}}if(M.length>0){M=M.sort(D);
M.get(0).focus()
}},this)
},applyFocus:function(){E(this.options.applyFocus)
},destroy:function(){F.destroy.call(this)
}});
var D=function(K,J){var I=H(E(K).attr("tabindex"),E(J).attr("tabindex"));
return(I!=0)?I:G(K,J)
};
var H=function(J,I){if(J){if(I){return J-I
}else{return -1
}}else{if(I){return +1
}else{return 0
}}};
var G=function(J,I){var K=A(J,I);
if(J==I){return 0
}else{if(K.parent==J){return -1
}else{if(K.parent==I){return +1
}else{return E(K.aPrevious).index()-E(K.bPrevious).index()
}}}};
var A=function(J,I){var M=E(J).add(E(J).parents()).get().reverse();
var L=E(I).add(E(I).parents()).get().reverse();
var K={aPrevious:J,bPrevious:I};
E.each(M,function(O,N){E.each(L,function(P,Q){if(N==Q){K.parent=N;
return false
}K.bPrevious=Q
});
if(K.parent){return false
}K.aPrevious=N
});
if(!K.parent){return null
}return K
};
C.ui.Focus.__fn={sortTabindex:D,sortTabindexNums:H,searchCommonParent:A,sortByDOMOrder:G};
var F=C.ui.Focus.$super
})(RichFaces.jQuery,RichFaces);;(function(B,A){A.ui=A.ui||{};
A.ui.PopupPanel.Border=function(H,F,G,E){C.constructor.call(this,H);
this.element=B(A.getDomElement(H));
this.element.css("cursor",G);
var D=this;
this.element.bind("mousedown",{border:D},this.startDrag);
this.modalPanel=F;
this.sizer=E
};
var C=A.BaseComponent.extend(A.ui.PopupPanel.Border);
var C=A.ui.PopupPanel.Border.$super;
B.extend(A.ui.PopupPanel.Border.prototype,(function(D){return{name:"RichFaces.ui.PopupPanel.Border",destroy:function(){if(this.doingDrag){B(document).unbind("mousemove",this.doDrag);
B(document).unbind("mouseup",this.endDrag)
}this.element.unbind("mousedown",this.startDrag);
this.element=null;
this.modalPanel=null
},show:function(){this.element.show()
},hide:function(){this.element.hide()
},startDrag:function(F){var E=F.data.border;
E.doingDrag=true;
E.dragX=F.clientX;
E.dragY=F.clientY;
B(document).bind("mousemove",{border:E},E.doDrag);
B(document).bind("mouseup",{border:E},E.endDrag);
E.modalPanel.startDrag(E);
E.onselectStartHandler=document.onselectstart;
document.onselectstart=function(){return false
}
},getWindowSize:function(){var F=0,E=0;
if(typeof (window.innerWidth)=="number"){F=window.innerWidth;
E=window.innerHeight
}else{if(document.documentElement&&(document.documentElement.clientWidth||document.documentElement.clientHeight)){F=document.documentElement.clientWidth;
E=document.documentElement.clientHeight
}else{if(document.body&&(document.body.clientWidth||document.body.clientHeight)){F=document.body.clientWidth;
E=document.body.clientHeight
}}}return{width:F,height:E}
},doDrag:function(E){var J=E.data.border;
if(!J.doingDrag){return 
}var I=E.clientX;
var F=E.clientY;
var L=J.getWindowSize();
if(I<0){I=0
}else{if(I>=L.width){I=L.width-1
}}if(F<0){F=0
}else{if(F>=L.height){F=L.height-1
}}var P=I-J.dragX;
var O=F-J.dragY;
if(P!=0||O!=0){var H=J.id;
var N=J.sizer.prototype.doDiff(P,O);
var M;
var K=J.modalPanel.cdiv;
if(N.deltaWidth||N.deltaHeight){M=J.modalPanel.invokeEvent("resize",E,null,K)
}else{if(N.deltaX||N.deltaY){M=J.modalPanel.invokeEvent("move",E,null,K)
}}var G;
if(M){G=J.modalPanel.doResizeOrMove(N)
}if(G){if(!G.x){J.dragX=I
}else{if(!N.deltaX){J.dragX-=G.vx||0
}else{J.dragX+=G.vx||0
}}if(!G.y){J.dragY=F
}else{if(!N.deltaY){J.dragY-=G.vy||0
}else{J.dragY+=G.vy||0
}}}}},endDrag:function(F){var E=F.data.border;
E.doingDrag=undefined;
B(document).unbind("mousemove",E.doDrag);
B(document).unbind("mouseup",E.endDrag);
E.modalPanel.endDrag(E);
document.onselectstart=E.onselectStartHandler;
E.onselectStartHandler=null
},doPosition:function(){this.sizer.prototype.doPosition(this.modalPanel,this.element)
}}
})())
})(RichFaces.jQuery,window.RichFaces);;(function(B,A){A.ui=A.ui||{};
A.ui.InputBase=function(F,D){C.constructor.call(this,F);
this.namespace=this.getNamespace()||"."+A.Event.createNamespace(this.getName(),this.getId());
this.namespace=this.namespace||"."+A.Event.createNamespace(this.name,this.id);
this.input=B(document.getElementById(F+"Input"));
this.attachToDom();
var E={};
E["keydown"+this.namespace]=B.proxy(this.__keydownHandler,this);
E["blur"+this.namespace]=B.proxy(this.__blurHandler,this);
E["change"+this.namespace]=B.proxy(this.__changeHandler,this);
E["focus"+this.namespace]=B.proxy(this.__focusHandler,this);
A.Event.bind(this.input,E,this)
};
A.BaseComponent.extend(A.ui.InputBase);
var C=A.ui.InputBase.$super;
B.extend(A.ui.InputBase.prototype,(function(){return{name:"inputBase",getName:function(){return this.name
},getNamespace:function(){return this.namespace
},__focusHandler:function(D){},__keydownHandler:function(D){},__blurHandler:function(D){},__changeHandler:function(D){},__setInputFocus:function(){this.input.focus()
},__getValue:function(){return this.input.val()
},__setValue:function(D){this.input.val(D);
if(this.defaultLabelClass){if(this.defaultLabel&&D==this.defaultLabel){this.input.addClass(this.defaultLabelClass)
}else{this.input.removeClass(this.defaultLabelClass)
}}},getValue:function(){return this.__getValue()
},setValue:function(D){this.__setValue(D)
},getInput:function(){return this.input
},getId:function(){return this.id
},destroy:function(){A.Event.unbindById(this.input,this.namespace);
this.input=null;
C.destroy.call(this)
}}
})())
})(RichFaces.jQuery,window.RichFaces);;/*
 * jQuery UI Mouse 1.11.2
 * http://jqueryui.com
 *
 * Copyright 2014 jQuery Foundation and other contributors
 * Released under the MIT license.
 * http://jquery.org/license
 *
 * http://api.jqueryui.com/mouse/
 */
(function(A){if(typeof define==="function"&&define.amd){define(["jquery","./widget"],A)
}else{A(jQuery)
}}(function(B){var A=false;
B(document).mouseup(function(){A=false
});
return B.widget("ui.mouse",{version:"1.11.2",options:{cancel:"input,textarea,button,select,option",distance:1,delay:0},_mouseInit:function(){var C=this;
this.element.bind("mousedown."+this.widgetName,function(D){return C._mouseDown(D)
}).bind("click."+this.widgetName,function(D){if(true===B.data(D.target,C.widgetName+".preventClickEvent")){B.removeData(D.target,C.widgetName+".preventClickEvent");
D.stopImmediatePropagation();
return false
}});
this.started=false
},_mouseDestroy:function(){this.element.unbind("."+this.widgetName);
if(this._mouseMoveDelegate){this.document.unbind("mousemove."+this.widgetName,this._mouseMoveDelegate).unbind("mouseup."+this.widgetName,this._mouseUpDelegate)
}},_mouseDown:function(E){if(A){return 
}this._mouseMoved=false;
(this._mouseStarted&&this._mouseUp(E));
this._mouseDownEvent=E;
var D=this,F=(E.which===1),C=(typeof this.options.cancel==="string"&&E.target.nodeName?B(E.target).closest(this.options.cancel).length:false);
if(!F||C||!this._mouseCapture(E)){return true
}this.mouseDelayMet=!this.options.delay;
if(!this.mouseDelayMet){this._mouseDelayTimer=setTimeout(function(){D.mouseDelayMet=true
},this.options.delay)
}if(this._mouseDistanceMet(E)&&this._mouseDelayMet(E)){this._mouseStarted=(this._mouseStart(E)!==false);
if(!this._mouseStarted){E.preventDefault();
return true
}}if(true===B.data(E.target,this.widgetName+".preventClickEvent")){B.removeData(E.target,this.widgetName+".preventClickEvent")
}this._mouseMoveDelegate=function(G){return D._mouseMove(G)
};
this._mouseUpDelegate=function(G){return D._mouseUp(G)
};
this.document.bind("mousemove."+this.widgetName,this._mouseMoveDelegate).bind("mouseup."+this.widgetName,this._mouseUpDelegate);
E.preventDefault();
A=true;
return true
},_mouseMove:function(C){if(this._mouseMoved){if(B.ui.ie&&(!document.documentMode||document.documentMode<9)&&!C.button){return this._mouseUp(C)
}else{if(!C.which){return this._mouseUp(C)
}}}if(C.which||C.button){this._mouseMoved=true
}if(this._mouseStarted){this._mouseDrag(C);
return C.preventDefault()
}if(this._mouseDistanceMet(C)&&this._mouseDelayMet(C)){this._mouseStarted=(this._mouseStart(this._mouseDownEvent,C)!==false);
(this._mouseStarted?this._mouseDrag(C):this._mouseUp(C))
}return !this._mouseStarted
},_mouseUp:function(C){this.document.unbind("mousemove."+this.widgetName,this._mouseMoveDelegate).unbind("mouseup."+this.widgetName,this._mouseUpDelegate);
if(this._mouseStarted){this._mouseStarted=false;
if(C.target===this._mouseDownEvent.target){B.data(C.target,this.widgetName+".preventClickEvent",true)
}this._mouseStop(C)
}A=false;
return false
},_mouseDistanceMet:function(C){return(Math.max(Math.abs(this._mouseDownEvent.pageX-C.pageX),Math.abs(this._mouseDownEvent.pageY-C.pageY))>=this.options.distance)
},_mouseDelayMet:function(){return this.mouseDelayMet
},_mouseStart:function(){},_mouseDrag:function(){},_mouseStop:function(){},_mouseCapture:function(){return true
}})
}));;/*
 * jQuery UI Effects Highlight 1.11.2
 * http://jqueryui.com
 *
 * Copyright 2014 jQuery Foundation and other contributors
 * Released under the MIT license.
 * http://jquery.org/license
 *
 * http://api.jqueryui.com/highlight-effect/
 */
(function(A){if(typeof define==="function"&&define.amd){define(["jquery","./effect"],A)
}else{A(jQuery)
}}(function(A){return A.effects.effect.highlight=function(G,B){var D=A(this),C=["backgroundImage","backgroundColor","opacity"],F=A.effects.setMode(D,G.mode||"show"),E={backgroundColor:D.css("backgroundColor")};
if(F==="hide"){E.opacity=0
}A.effects.save(D,C);
D.show().css({backgroundImage:"none",backgroundColor:G.color||"#ffff99"}).animate(E,{queue:false,duration:G.duration,easing:G.easing,complete:function(){if(F==="hide"){D.hide()
}A.effects.restore(D,C);
B()
}})
}
}));;(function(C,B){B.ui=B.ui||{};
var A={expanded:false,stylePrefix:"rf-pm-gr",expandEvent:"click",collapseEvent:"click",selectable:false,unselectable:false};
var E={exec:function(G,F){var H=G.mode;
if(H=="server"){return this.execServer(G)
}else{if(H=="ajax"){return this.execAjax(G)
}else{if(H=="client"||H=="none"){return this.execClient(G,F)
}else{B.log.error("EXPAND_ITEM.exec : unknown mode ("+H+")")
}}}},execServer:function(F){F.__changeState();
B.submitForm(this.__getParentForm(F),F.options.ajax["parameters"]||{});
return false
},execAjax:function(G){var F=G.__changeState();
B.ajax(G.id,null,C.extend({},G.options.ajax,{}));
G.__restoreState(F);
return true
},execClient:function(G,F){if(F){G.__expand()
}else{G.__collapse()
}return G.__fireEvent("switch")
},__getParentForm:function(F){return C(C(B.getDomElement(F.id)).parents("form")[0])
}};
B.ui.PanelMenuGroup=B.ui.PanelMenuItem.extendClass({name:"PanelMenuGroup",init:function(G,F){D.constructor.call(this,G,C.extend({},A,F||{}));
this.options.bubbleSelection=this.__rfPanelMenu().options.bubbleSelection;
this.options.expandSingle=this.__rfPanelMenu().options.expandSingle;
if(!this.options.disabled){var H=this;
if(!this.options.selectable){if(this.options.expandEvent==this.options.collapseEvent){this.__header().bind(this.options.expandEvent,function(){H.switchExpantion()
})
}else{this.__header().bind(this.options.expandEvent,function(){if(H.collapsed()){return H.expand()
}});
this.__header().bind(this.options.collapseEvent,function(){if(H.expanded()){return H.collapse()
}})
}}else{if(this.options.expandEvent==this.options.collapseEvent){if(this.options.expandEvent!="click"){this.__header().bind(this.options.expandEvent,function(){H.switchExpantion()
})
}}else{if(this.options.expandEvent!="click"){this.__header().bind(this.options.expandEvent,function(){if(H.collapsed()){return H.expand()
}})
}if(this.options.collapseEvent!="click"){this.__header().bind(this.options.collapseEvent,function(){if(H.expanded()){return H.collapse()
}})
}}}if(this.options.selectable||this.options.bubbleSelection){this.__content().bind("select",function(I){if(H.options.selectable&&H.__isMyEvent(I)){H.expand()
}if(H.options.bubbleSelection&&!H.__isMyEvent(I)){H.__select();
if(!H.expanded()){H.expand()
}}});
this.__content().bind("unselect",function(I){if(H.options.selectable&&H.__isMyEvent(I)){H.collapse()
}if(H.options.bubbleSelection&&!H.__isMyEvent(I)){H.__unselect()
}})
}}},expanded:function(){return this.__getExpandValue()
},expand:function(){if(this.expanded()){return 
}if(!this.__fireEvent("beforeexpand")){return false
}E.exec(this,true)
},__expand:function(){this.__updateStyles(true);
this.__collapseForExpandSingle();
return this.__fireEvent("expand")
},collapsed:function(){return !this.__getExpandValue()
},collapse:function(){if(!this.expanded()){return 
}if(!this.__fireEvent("beforecollapse")){return false
}E.exec(this,false)
},__collapse:function(){this.__updateStyles(false);
this.__childGroups().each(function(F,G){B.component(G.id).__collapse()
});
return this.__fireEvent("collapse")
},__updateStyles:function(F){if(F){this.__content().removeClass("rf-pm-colps").addClass("rf-pm-exp");
this.__header().removeClass("rf-pm-hdr-colps").addClass("rf-pm-hdr-exp");
this.__setExpandValue(true)
}else{this.__content().addClass("rf-pm-colps").removeClass("rf-pm-exp");
this.__header().addClass("rf-pm-hdr-colps").removeClass("rf-pm-hdr-exp");
this.__setExpandValue(false)
}},switchExpantion:function(){var F=this.__fireEvent("beforeswitch");
if(!F){return false
}if(this.expanded()){this.collapse()
}else{this.expand()
}},onCompleteHandler:function(){if(this.options.selectable){D.onCompleteHandler.call(this)
}E.execClient(this,this.expanded())
},__switch:function(F){if(F){this.__expand()
}else{this.__collapse()
}return this.__fireEvent("switch")
},__childGroups:function(){return this.__content().children(".rf-pm-gr")
},__group:function(){return C(B.getDomElement(this.id))
},__header:function(){return C(B.getDomElement(this.id+":hdr"))
},__content:function(){return C(B.getDomElement(this.id+":cnt"))
},__expandValueInput:function(){return document.getElementById(this.id+":expanded")
},__getExpandValue:function(){return this.__expandValueInput().value=="true"
},__collapseForExpandSingle:function(){if(this.options.expandSingle){this.__rfPanelMenu().__collapseGroups(this)
}},__setExpandValue:function(H){var F=this.__expandValueInput();
var G=F.value;
F.value=H;
return G
},__changeState:function(){if(!this.__getExpandValue()){this.__collapseForExpandSingle()
}var F={};
F.expanded=this.__setExpandValue(!this.__getExpandValue());
if(this.options.selectable){F.itemName=this.__rfPanelMenu().selectedItem(this.itemName)
}return F
},__restoreState:function(F){if(!F){return 
}if(F.expanded){this.__setExpandValue(F.expanded)
}if(F.itemName){this.__rfPanelMenu().selectedItem(F.itemName)
}},__isMyEvent:function(F){return this.id==F.target.id
},destroy:function(){B.Event.unbindById(this.id,"."+this.namespace);
D.destroy.call(this)
}});
var D=B.ui.PanelMenuGroup.$super
})(RichFaces.jQuery,RichFaces);;(function(F,I){I.ui=I.ui||{};
var E={styleClass:"",nonblocking:false,nonblockingOpacity:0.2,showHistory:false,animationSpeed:"slow",opacity:"1",showShadow:false,showCloseButton:true,appearAnimation:"fade",hideAnimation:"fade",sticky:false,stayTime:8000,delay:0};
var H="org.richfaces.notifyStack.default";
var J="click dblclick  keydown keypress keyup mousedown mousemove mouseout mouseover mouseup";
var K={summary:"pnotify_title",detail:"pnotify_text",styleClass:"pnotify_addclass",nonblocking:"pnotify_nonblock",nonblockingOpacity:"pnotify_nonblock_opacity",showHistory:"pnotify_history",animation:"pnotify_animation",appearAnimation:"effect_in",hideAnimation:"effect_out",animationSpeed:"pnotify_animate_speed",opacity:"pnotify_opacity",showShadow:"pnotify_shadow",showCloseButton:"pnotify_closer",sticky:"pnotify_hide",stayTime:"pnotify_delay"};
var B=["rf-ntf-inf","rf-ntf-wrn","rf-ntf-err","rf-ntf-ftl"];
var G=function(O,N,P){for(var L in N){var M=P[L]!=null?P[L]:L;
O[M]=N[L];
if(O[M] instanceof Object){O[M]=F.extend({},O[M],P)
}}return O
};
var D=function(){if(!document.getElementById(H)){var L=F('<span id="'+H+'" class="rf-ntf-stck" />');
F("body").append(L);
new I.ui.NotifyStack(H)
}return C(H)
};
var C=function(L){if(!L){return D()
}return I.component(L).getStack()
};
var A=function(O,N,M){var L=O.slice((M||N)+1||O.length);
O.length=N<0?O.length+N:N;
return O.push.apply(O,L)
};
I.ui.Notify=function(M){var M=F.extend({},E,M);
if(typeof M.severity=="number"){var L=B[M.severity];
M.styleClass=M.styleClass?L+" "+M.styleClass:L
}var N=G({},M,K);
var O=function(){var P=C(M.stackId);
N.pnotify_stack=P;
N.pnotify_addclass+=" rf-ntf-pos-"+P.position;
N.pnotify_after_close=function(R){var S=F.inArray(R,P.notifications);
if(S>=0){A(P.notifications,S)
}};
var Q=F.pnotify(N);
Q.on(J,function(R){if(M["on"+R.type]){M["on"+R.type].call(this,R)
}});
P.addNotification(Q)
};
if(M.sticky!==null){N.pnotify_hide=!M.sticky
}F(document).ready(function(){if(M.delay){setTimeout(function(){O()
},M.delay)
}else{O()
}})
}
})(RichFaces.jQuery,RichFaces);;(function(C,K){K.ui=K.ui||{};
K.ui.AutocompleteBase=function(U,V,S,T){P.constructor.call(this,U);
this.selectId=V;
this.fieldId=S;
this.options=C.extend({},O,T);
this.namespace=this.namespace||"."+K.Event.createNamespace(this.name,this.selectId);
this.currentValue=C(K.getDomElement(S)).val();
this.tempValue=this.getValue();
this.isChanged=this.tempValue.length!=0;
J.call(this)
};
K.BaseComponent.extend(K.ui.AutocompleteBase);
var P=K.ui.AutocompleteBase.$super;
var O={changeDelay:8};
var J=function(){var S={};
if(this.options.buttonId){S["mousedown"+this.namespace]=I;
S["mouseup"+this.namespace]=E;
K.Event.bindById(this.options.buttonId,S,this)
}S={};
S["focus"+this.namespace]=B;
S["blur"+this.namespace]=H;
S["click"+this.namespace]=D;
S["keydown"+this.namespace]=A;
S["change"+this.namespace]=function(T){if(this.focused){T.stopPropagation()
}};
K.Event.bindById(this.fieldId,S,this);
S={};
S["mousedown"+this.namespace]=N;
S["mouseup"+this.namespace]=E;
K.Event.bindById(this.selectId,S,this)
};
var N=function(){this.isMouseDown=true
};
var E=function(){K.getDomElement(this.fieldId).focus()
};
var I=function(S){this.isMouseDown=true;
if(this.timeoutId){window.clearTimeout(this.timeoutId);
this.timeoutId=null
}K.getDomElement(this.fieldId).focus();
if(this.isVisible){this.__hide(S)
}else{L.call(this,S)
}};
var B=function(S){if(!this.focused){this.__focusValue=this.getValue();
this.focused=true;
this.invokeEvent("focus",K.getDomElement(this.fieldId),S)
}};
var H=function(S){if(this.isMouseDown){K.getDomElement(this.fieldId).focus();
this.isMouseDown=false
}else{if(!this.isMouseDown){if(this.isVisible){var T=this;
this.timeoutId=window.setTimeout(function(){if(T.isVisible){T.__hide(S)
}},200)
}if(this.focused){this.focused=false;
this.invokeEvent("blur",K.getDomElement(this.fieldId),S);
if(this.__focusValue!=this.getValue()){this.invokeEvent("change",K.getDomElement(this.fieldId),S)
}}}}};
var D=function(S){};
var M=function(T){if(this.isChanged){if(this.getValue()==this.tempValue){return 
}}this.isChanged=false;
var U=this.getValue();
var S=U!=this.currentValue;
if(T.keyCode==K.KEYS.LEFT||T.keyCode==K.KEYS.RIGHT||S){if(S){this.currentValue=this.getValue();
this.__onChangeValue(T,undefined,(!this.isVisible?this.__show:undefined))
}else{if(this.isVisible){this.__onChangeValue(T)
}}}};
var L=function(S){if(this.isChanged){this.isChanged=false;
M.call(this,{})
}else{!this.__updateState(S)&&this.__show(S)
}};
var A=function(S){switch(S.keyCode){case K.KEYS.UP:S.preventDefault();
if(this.isVisible){this.__onKeyUp(S)
}break;
case K.KEYS.DOWN:S.preventDefault();
if(this.isVisible){this.__onKeyDown(S)
}else{L.call(this,S)
}break;
case K.KEYS.PAGEUP:if(this.isVisible){S.preventDefault();
this.__onPageUp(S)
}break;
case K.KEYS.PAGEDOWN:if(this.isVisible){S.preventDefault();
this.__onPageDown(S)
}break;
case K.KEYS.HOME:if(this.isVisible){S.preventDefault();
this.__onKeyHome(S)
}break;
case K.KEYS.END:if(this.isVisible){S.preventDefault();
this.__onKeyEnd(S)
}break;
case K.KEYS.RETURN:if(this.isVisible){S.preventDefault();
this.__onEnter(S);
this.__hide(S);
return false
}break;
case K.KEYS.ESC:this.__hide(S);
break;
default:if(!this.options.selectOnly){var T=this;
window.clearTimeout(this.changeTimerId);
this.changeTimerId=window.setTimeout(function(){M.call(T,S)
},this.options.changeDelay)
}break
}};
var Q=function(T){if(!this.isVisible){if(this.__onBeforeShow(T)!=false){this.scrollElements=K.Event.bindScrollEventHandlers(this.selectId,this.__hide,this,this.namespace);
var S=K.getDomElement(this.selectId);
if(this.options.attachToBody){this.parentElement=S.parentNode;
document.body.appendChild(S)
}C(S).setPosition({id:this.fieldId},{type:"DROPDOWN"}).show();
this.isVisible=true;
this.__onShow(T)
}}};
var G=function(S){if(this.isVisible){this.__conceal();
this.isVisible=false;
this.__onHide(S)
}};
var R=function(){if(this.isVisible){if(this.scrollElements){K.Event.unbindScrollEventHandlers(this.scrollElements,this);
this.scrollElements=null
}C(K.getDomElement(this.selectId)).hide();
if(this.options.attachToBody&&this.parentElement){this.parentElement.appendChild(K.getDomElement(this.selectId));
this.parentElement=null
}}};
var F=function(S){if(this.fieldId){K.getDomElement(this.fieldId).value=S;
return S
}else{return""
}};
C.extend(K.ui.AutocompleteBase.prototype,(function(){return{name:"AutocompleteBase",showPopup:function(S){if(!this.focused){K.getDomElement(this.fieldId).focus()
}L.call(this,S)
},hidePopup:function(S){this.__hide(S)
},getNamespace:function(){return this.namespace
},getValue:function(){return this.fieldId?K.getDomElement(this.fieldId).value:""
},setValue:function(S){if(S==this.currentValue){return 
}F.call(this,S);
this.isChanged=true
},__updateInputValue:F,__show:Q,__hide:G,__conceal:R,__onChangeValue:function(S){},__onKeyUp:function(S){},__onKeyDown:function(S){},__onPageUp:function(S){},__onPageDown:function(S){},__onKeyHome:function(S){},__onKeyEnd:function(S){},__onBeforeShow:function(S){},__onShow:function(S){},__onHide:function(S){},destroy:function(){this.parentNode=null;
if(this.scrollElements){K.Event.unbindScrollEventHandlers(this.scrollElements,this);
this.scrollElements=null
}this.options.buttonId&&K.Event.unbindById(this.options.buttonId,this.namespace);
K.Event.unbindById(this.fieldId,this.namespace);
K.Event.unbindById(this.selectId,this.namespace);
P.destroy.call(this)
}}
})())
})(RichFaces.jQuery,RichFaces);;(function(D,B,A){B.push={options:{transport:"websocket",fallbackTransport:"long-polling",logLevel:"info"},_subscribedTopics:{},_addedTopics:{},_removedTopics:{},_handlersCounter:{},_pushSessionId:null,_lastMessageNumber:-1,_pushResourceUrl:null,_pushHandlerUrl:null,updateConnection:function(){if(D.isEmptyObject(this._handlersCounter)){this._disconnect()
}else{if(!D.isEmptyObject(this._addedTopics)||!D.isEmptyObject(this._removedTopics)){this._disconnect();
this._connect()
}}this._addedTopics={};
this._removedTopics={}
},increaseSubscriptionCounters:function(F){if(isNaN(this._handlersCounter[F]++)){this._handlersCounter[F]=1;
this._addedTopics[F]=true
}},decreaseSubscriptionCounters:function(F){if(--this._handlersCounter[F]==0){delete this._handlersCounter[F];
this._removedTopics[F]=true;
this._subscribedTopics[F]=false
}},setPushResourceUrl:function(F){this._pushResourceUrl=C(F)
},setPushHandlerUrl:function(F){this._pushHandlerUrl=C(F)
},_messageCallback:function(F){if(F.state&&F.state==="opening"){this._lastMessageNumber=-1;
return 
}var G=/^(<!--[^>]+-->\s*)+/;
var K=/<msg topic="([^"]+)" number="([^"]+)">([^<]*)<\/msg>/g;
var H=F.responseBody.replace(G,"");
if(H){var L;
while(L=K.exec(H)){if(!L[1]){continue
}var J={topic:L[1],number:parseInt(L[2]),data:D.parseJSON(L[3])};
if(J.number<=this._lastMessageNumber){continue
}var I=new jQuery.Event("push.push.RICH."+J.topic,{rf:{data:J.data}});
(function(M){D(function(){D(document).trigger(M)
})
})(I);
this._lastMessageNumber=J.number
}}},_errorCallback:function(G){for(var F in this.newlySubcribed){this._subscribedTopics[F]=true;
D(document).trigger("error.push.RICH."+F,G)
}},_connect:function(){this.newlySubcribed={};
var H=[];
for(var F in this._handlersCounter){H.push(F);
if(!this._subscribedTopics[F]){this.newlySubcribed[F]=true
}}var G={pushTopic:H};
if(this._pushSessionId){G.forgetPushSessionId=this._pushSessionId
}D.ajax({data:G,dataType:"text",traditional:true,type:"POST",url:this._pushResourceUrl,success:D.proxy(function(K){var N=D.parseJSON(K);
for(var I in N.failures){D(document).trigger("error.push.RICH."+I)
}if(N.sessionId){this._pushSessionId=N.sessionId;
var M=this._pushHandlerUrl||this._pushResourceUrl;
M+="?__richfacesPushAsync=1&pushSessionId=";
M+=this._pushSessionId;
var L=D.proxy(this._messageCallback,this);
var J=D.proxy(this._errorCallback,this);
D.atmosphere.subscribe(M,L,{transport:this.options.transport,fallbackTransport:this.options.fallbackTransport,logLevel:this.options.logLevel,onError:J});
for(var I in this.newlySubcribed){this._subscribedTopics[I]=true;
D(document).trigger("subscribed.push.RICH."+I)
}}},this)})
},_disconnect:function(){D.atmosphere.unsubscribe()
}};
D.fn.richpush=function(F){var G=D.extend({},D.fn.richpush);
return this.each(function(){G.element=this;
G.options=D.extend({},G.options,F);
G.eventNamespace=".push.RICH."+G.element.id;
G._create();
D(document).on("beforeDomClean"+G.eventNamespace,function(H){if(H.target&&(H.target===G.element||D.contains(H.target,G.element))){G._destroy()
}})
})
};
D.extend(D.fn.richpush,{options:{address:null,subscribed:null,push:null,error:null},_create:function(){var F=this;
this.address=this.options.address;
this.handlers={subscribed:null,push:null,error:null};
D.each(this.handlers,function(G){if(F.options[G]){var H=function(I,J){if(J){D.extend(I,{rf:{data:J}})
}F.options[G].call(F.element,I)
};
F.handlers[G]=H;
D(document).on(G+F.eventNamespace+"."+F.address,H)
}});
B.push.increaseSubscriptionCounters(this.address)
},_destroy:function(){B.push.decreaseSubscriptionCounters(this.address);
D(document).off(this.eventNamespace)
}});
D(document).ready(function(){B.push.updateConnection()
});
A.ajax.addOnEvent(E);
A.ajax.addOnError(E);
function E(F){if(F.type=="event"){if(F.status!="success"){return 
}}else{if(F.type!="error"){return 
}}B.push.updateConnection()
}function C(G){var F=G;
if(G.charAt(0)=="/"){F=location.protocol+"//"+location.host+G
}return F
}}(RichFaces.jQuery,RichFaces,jsf));;(function(C,B){B.ui=B.ui||{};
B.ui.Tab=B.ui.TogglePanelItem.extendClass({name:"Tab",init:function(F,E){D.constructor.call(this,F,E);
this.attachToDom();
this.index=E.index;
this.getTogglePanel().getItems()[this.index]=this
},__header:function(F){var G=C(B.getDomElement(this.id+":header"));
for(var E in A){if(E!==F){G.removeClass(A[E])
}if(!G.hasClass(A[F])){G.addClass(A[F])
}}return G
},__content:function(){if(!this.__content_){this.__content_=C(B.getDomElement(this.id))
}return this.__content_
},__enter:function(){this.__content().show();
this.__header("active");
return this.__fireEnter()
},__fireLeave:function(){return B.Event.fireById(this.id+":content","leave")
},__fireEnter:function(){return B.Event.fireById(this.id+":content","enter")
},__addUserEventHandler:function(F){var G=this.options["on"+F];
if(G){var E=B.Event.bindById(this.id+":content",F,G)
}},getHeight:function(E){if(E||!this.__height){this.__height=C(B.getDomElement(this.id)).outerHeight(true)
}return this.__height
},__leave:function(){var E=this.__fireLeave();
if(!E){return false
}this.__content().hide();
this.__header("inactive");
return true
},destroy:function(){var E=this.getTogglePanel();
if(E&&E.getItems&&E.getItems()[this.index]){delete E.getItems()[this.index]
}B.Event.unbindById(this.id);
D.destroy.call(this)
}});
var D=B.ui.Tab.$super;
var A={active:"rf-tab-hdr-act",inactive:"rf-tab-hdr-inact",disabled:"rf-tab-hdr-dis"}
})(RichFaces.jQuery,RichFaces);;(function(B,A){A.ui=A.ui||{};
A.ui.PopupPanel.Sizer=function(G,E,F,D){C.constructor.call(this,G)
};
var C=A.BaseComponent.extend(A.ui.PopupPanel.Sizer);
var C=A.ui.PopupPanel.Sizer.$super;
B.extend(A.ui.PopupPanel.Sizer.prototype,(function(D){return{name:"richfaces.ui.PopupPanel.Sizer",doSetupSize:function(J,F){var H=0;
var E=0;
var G=B(A.getDomElement(F));
var I=J.reductionData;
if(I){if(I.w){H=I.w/2
}if(I.h){E=I.h/2
}}if(H>0){if(F.clientWidth>H){if(!F.reducedWidth){F.reducedWidth=G.css("width")
}G.css("width",H+"px")
}else{if(H<4&&F.reducedWidth==4+"px"){G.css("width",H+"px")
}}}else{if(F.reducedWidth){G.css("width",F.reducedWidth);
F.reducedWidth=undefined
}}if(E>0){if(F.clientHeight>E){if(!F.reducedHeight){F.reducedHeight=G.css("height")
}F.style.height=E+"px"
}else{if(E<4&&F.reducedHeight==4+"px"){G.css("height",E+"px")
}}}else{if(F.reducedHeight){G.css("height",F.reducedHeight);
F.reducedHeight=undefined
}}},doSetupPosition:function(I,E,H,G){var F=B(A.getDomElement(E));
if(!isNaN(H)&&!isNaN(G)){F.css("left",H+"px");
F.css("top",G+"px")
}},doPosition:function(F,E){},doDiff:function(F,E){}}
})());
A.ui.PopupPanel.Sizer.Diff=function(F,D,E,G){this.deltaX=F;
this.deltaY=D;
this.deltaWidth=E;
this.deltaHeight=G
};
A.ui.PopupPanel.Sizer.Diff.EMPTY=new A.ui.PopupPanel.Sizer.Diff(0,0,0,0),A.ui.PopupPanel.Sizer.N=function(){};
B.extend(A.ui.PopupPanel.Sizer.N.prototype,A.ui.PopupPanel.Sizer.prototype);
B.extend(A.ui.PopupPanel.Sizer.N.prototype,{name:"richfaces.ui.PopupPanel.Sizer.N",doPosition:function(F,D){var E=B(A.getDomElement(D));
E.css("width",F.width()+"px");
this.doSetupPosition(F,D,0,0)
},doDiff:function(E,D){return new A.ui.PopupPanel.Sizer.Diff(0,D,0,-D)
}});
A.ui.PopupPanel.Sizer.NW=function(){};
B.extend(A.ui.PopupPanel.Sizer.NW.prototype,A.ui.PopupPanel.Sizer.prototype);
B.extend(A.ui.PopupPanel.Sizer.NW.prototype,{name:"richfaces.ui.PopupPanel.Sizer.NW",doPosition:function(E,D){this.doSetupSize(E,D);
this.doSetupPosition(E,D,0,0)
},doDiff:function(E,D){return new A.ui.PopupPanel.Sizer.Diff(E,D,-E,-D)
}});
A.ui.PopupPanel.Sizer.NE=function(){};
B.extend(A.ui.PopupPanel.Sizer.NE.prototype,A.ui.PopupPanel.Sizer.prototype);
B.extend(A.ui.PopupPanel.Sizer.NE.prototype,{name:"richfaces.ui.PopupPanel.Sizer.NE",doPosition:function(E,D){this.doSetupSize(E,D);
this.doSetupPosition(E,D,E.width()-D.clientWidth,0)
},doDiff:function(E,D){return new A.ui.PopupPanel.Sizer.Diff(0,D,E,-D)
}});
A.ui.PopupPanel.Sizer.E=function(){};
B.extend(A.ui.PopupPanel.Sizer.E.prototype,A.ui.PopupPanel.Sizer.prototype);
B.extend(A.ui.PopupPanel.Sizer.E.prototype,{name:"richfaces.ui.PopupPanel.Sizer.E",doPosition:function(F,D){var E=B(A.getDomElement(D));
E.css("height",F.height()+"px");
this.doSetupPosition(F,D,F.width()-D.clientWidth,0)
},doDiff:function(E,D){return new A.ui.PopupPanel.Sizer.Diff(0,0,E,0)
}});
A.ui.PopupPanel.Sizer.SE=function(){};
B.extend(A.ui.PopupPanel.Sizer.SE.prototype,A.ui.PopupPanel.Sizer.prototype);
B.extend(A.ui.PopupPanel.Sizer.SE.prototype,{name:"richfaces.ui.PopupPanel.Sizer.SE",doPosition:function(E,D){this.doSetupSize(E,D);
this.doSetupPosition(E,D,E.width()-D.clientWidth,E.height()-D.clientHeight)
},doDiff:function(E,D){return new A.ui.PopupPanel.Sizer.Diff(0,0,E,D)
}});
A.ui.PopupPanel.Sizer.S=function(){};
B.extend(A.ui.PopupPanel.Sizer.S.prototype,A.ui.PopupPanel.Sizer.prototype);
B.extend(A.ui.PopupPanel.Sizer.S.prototype,{name:"richfaces.ui.PopupPanel.Sizer.S",doPosition:function(F,D){var E=B(A.getDomElement(D));
E.css("width",F.width()+"px");
this.doSetupPosition(F,D,0,F.height()-D.clientHeight)
},doDiff:function(E,D){return new A.ui.PopupPanel.Sizer.Diff(0,0,0,D)
}});
A.ui.PopupPanel.Sizer.SW=function(){};
B.extend(A.ui.PopupPanel.Sizer.SW.prototype,A.ui.PopupPanel.Sizer.prototype);
B.extend(A.ui.PopupPanel.Sizer.SW.prototype,{name:"richfaces.ui.PopupPanel.Sizer.SW",doPosition:function(E,D){this.doSetupSize(E,D);
this.doSetupPosition(E,D,0,E.height()-D.clientHeight)
},doDiff:function(E,D){return new A.ui.PopupPanel.Sizer.Diff(E,0,-E,D)
}});
A.ui.PopupPanel.Sizer.W=function(){};
B.extend(A.ui.PopupPanel.Sizer.W.prototype,A.ui.PopupPanel.Sizer.prototype);
B.extend(A.ui.PopupPanel.Sizer.W.prototype,{name:"richfaces.ui.PopupPanel.Sizer.W",doPosition:function(F,D){var E=B(A.getDomElement(D));
E.css("height",F.height()+"px");
this.doSetupPosition(F,D,0,0)
},doDiff:function(E,D){return new A.ui.PopupPanel.Sizer.Diff(E,0,-E,0)
}});
A.ui.PopupPanel.Sizer.Header=function(){};
B.extend(A.ui.PopupPanel.Sizer.Header.prototype,A.ui.PopupPanel.Sizer.prototype);
B.extend(A.ui.PopupPanel.Sizer.Header.prototype,{name:"richfaces.ui.PopupPanel.Sizer.Header",doPosition:function(E,D){},doDiff:function(E,D){return new A.ui.PopupPanel.Sizer.Diff(E,D,0,0)
}})
})(RichFaces.jQuery,window.RichFaces);;(function(B,A){A.ui=A.ui||{};
A.ui.AccordionItem=A.ui.TogglePanelItem.extendClass({name:"AccordionItem",init:function(E,D){C.constructor.call(this,E,D);
if(!this.disabled){A.Event.bindById(this.id+":header","click",this.__onHeaderClick,this)
}if(this.isSelected()){var F=this;
B(document).one("javascriptServiceComplete",function(){F.__fitToHeight(F.getTogglePanel())
})
}},__onHeaderClick:function(D){this.getTogglePanel().switchToItem(this.getName())
},__header:function(){return B(A.getDomElement(this.id+":header"))
},__content:function(){if(!this.__content_){this.__content_=B(A.getDomElement(this.id+":content"))
}return this.__content_
},__enter:function(){var D=this.getTogglePanel();
if(D.isKeepHeight){this.__content().hide();
this.__fitToHeight(D)
}this.__content().show();
this.__header().addClass("rf-ac-itm-hdr-act").removeClass("rf-ac-itm-hdr-inact");
return this.__fireEnter()
},__fitToHeight:function(D){var G=D.getInnerHeight();
var E=D.getItems();
for(var F in E){G-=E[F].__header().outerHeight()
}this.__content().height(G-20)
},getHeight:function(D){if(D||!this.__height){this.__height=B(A.getDomElement(this.id)).outerHeight(true)
}return this.__height
},__leave:function(){var D=this.__fireLeave();
if(!D){return false
}this.__content().hide();
this.__header().removeClass("rf-ac-itm-hdr-act").addClass("rf-ac-itm-hdr-inact");
return true
}});
var C=A.ui.AccordionItem.$super
})(RichFaces.jQuery,RichFaces);;(function(B,A){A.ui=A.ui||{};
A.ui.CollapsiblePanel=A.ui.TogglePanel.extendClass({name:"CollapsiblePanel",init:function(E,D){A.ui.TogglePanel.call(this,E,D);
this.switchMode=D.switchMode;
this.__addUserEventHandler("beforeswitch");
this.__addUserEventHandler("switch");
this.options.cycledSwitching=true;
var C=this;
B(document.getElementById(this.id)).ready(function(){A.Event.bindById(C.id+":header","click",C.__onHeaderClick,C);
new RichFaces.ui.CollapsiblePanelItem(C.id+":content",{index:0,togglePanelId:C.id,switchMode:C.switchMode,name:"true"}),new RichFaces.ui.CollapsiblePanelItem(C.id+":empty",{index:1,togglePanelId:C.id,switchMode:C.switchMode,name:"false"})
})
},switchPanel:function(C){this.switchToItem(C||"@next")
},__onHeaderClick:function(){this.switchToItem("@next")
},__fireItemChange:function(D,C){return new A.Event.fireById(this.id,"switch",{id:this.id,isExpanded:C.getName()})
},__fireBeforeItemChange:function(D,C){return A.Event.fireById(this.id,"beforeswitch",{id:this.id,isExpanded:C.getName()})
}})
})(RichFaces.jQuery,RichFaces);;(function(D,C){C.ui=C.ui||{};
var A={toolbar:"Basic",skin:"moono",readonly:false,style:"",styleClass:"",editorStyle:"",editorClass:"",width:"100%",height:"200px"};
var B=["key","paste","undo","redo"];
C.ui.Editor=function(H,G,F){E.constructor.call(this,H);
this.options=D.extend({},A,G);
this.componentId=H;
this.textareaId=H+":inp";
this.editorElementId="cke_"+this.textareaId;
this.valueChanged=false;
this.dirtyState=false;
this.config=D.extend({},F);
this.attachToDom(this.componentId);
D(document).ready(D.proxy(this.__initializationHandler,this));
C.Event.bindById(this.__getTextarea(),"init",this.options.oninit,this);
C.Event.bindById(this.__getTextarea(),"dirty",this.options.ondirty,this)
};
C.BaseComponent.extend(C.ui.Editor);
var E=C.ui.Editor.$super;
D.extend(C.ui.Editor.prototype,{name:"Editor",__initializationHandler:function(){this.ckeditor=CKEDITOR.replace(this.textareaId,this.__getConfiguration());
if(this.__getForm()){this.__updateTextareaHandlerWrapper=C.Event.bind(this.__getForm(),"ajaxsubmit",D.proxy(this.__updateTextareaHandler,this))
}this.ckeditor.on("instanceReady",D.proxy(this.__instanceReadyHandler,this));
this.ckeditor.on("blur",D.proxy(this.__blurHandler,this));
this.ckeditor.on("focus",D.proxy(this.__focusHandler,this));
for(var F in B){this.ckeditor.on(B[F],D.proxy(this.__checkDirtyHandlerWithDelay,this))
}this.dirtyCheckingInterval=window.setInterval(D.proxy(this.__checkDirtyHandler,this),100)
},__checkDirtyHandlerWithDelay:function(){window.setTimeout(D.proxy(this.__checkDirtyHandler,this),0)
},__checkDirtyHandler:function(){if(this.ckeditor.checkDirty()){this.dirtyState=true;
this.valueChanged=true;
this.ckeditor.resetDirty();
this.__dirtyHandler()
}},__dirtyHandler:function(){this.invokeEvent.call(this,"dirty",document.getElementById(this.textareaId))
},__updateTextareaHandler:function(){this.ckeditor.updateElement()
},__instanceReadyHandler:function(F){this.__setupStyling();
this.__setupPassThroughAttributes();
this.invokeEvent.call(this,"init",document.getElementById(this.textareaId),F)
},__blurHandler:function(F){this.invokeEvent.call(this,"blur",document.getElementById(this.textareaId),F);
if(this.isDirty()){this.valueChanged=true;
this.__changeHandler()
}this.dirtyState=false
},__focusHandler:function(F){this.invokeEvent.call(this,"focus",document.getElementById(this.textareaId),F)
},__changeHandler:function(F){this.invokeEvent.call(this,"change",document.getElementById(this.textareaId),F)
},__getTextarea:function(){return D(document.getElementById(this.textareaId))
},__getForm:function(){return D("form").has(this.__getTextarea()).get(0)
},__getConfiguration:function(){var F=this.__getTextarea();
return D.extend({skin:this.options.skin,toolbar:this.__getToolbar(),readOnly:F.attr("readonly")||this.options.readonly,width:this.__resolveUnits(this.options.width),height:this.__resolveUnits(this.options.height),bodyClass:"rf-ed-b",defaultLanguage:this.options.lang,contentsLanguage:this.options.lang},this.config)
},__setupStyling:function(){var H=D(document.getElementById(this.editorElementId));
if(!H.hasClass("rf-ed")){H.addClass("rf-ed")
}var F=D.trim(this.options.styleClass+" "+this.options.editorClass);
if(this.initialStyle==undefined){this.initialStyle=H.attr("style")
}var G=this.__concatStyles(this.initialStyle,this.options.style,this.options.editorStyle);
if(this.oldStyleClass!==F){if(this.oldStyleClass){H.removeClass(this.oldStyleClass)
}H.addClass(F);
this.oldStyleClass=F
}if(this.oldStyle!==G){H.attr("style",G);
this.oldStyle=G
}},__setupPassThroughAttributes:function(){var F=this.__getTextarea();
var G=D(document.getElementById(this.editorElementId));
G.attr("title",F.attr("title"))
},__concatStyles:function(){var F="";
for(var G=0;
G<arguments.length;
G++){var H=D.trim(arguments[G]);
if(H){F=F+H+"; "
}}return F
},__getToolbar:function(){var G=this.options.toolbar;
var F=G.toLowerCase();
if(F==="basic"){return"Basic"
}if(F==="full"){return"Full"
}return G
},__setOptions:function(F){this.options=D.extend({},A,F)
},__resolveUnits:function(F){var F=D.trim(F);
if(F.match(/^[0-9]+$/)){return F+"px"
}else{return F
}},getEditor:function(){return this.ckeditor
},setValue:function(F){this.ckeditor.setData(F,D.proxy(function(){this.valueChanged=false;
this.dirtyState=false;
this.ckeditor.resetDirty()
},this))
},getValue:function(){return this.ckeditor.getData()
},getInput:function(){return document.getElementById(this.textareaId)
},focus:function(){this.ckeditor.focus()
},blur:function(){this.ckeditor.focusManager.blur(true)
},isFocused:function(){return this.ckeditor.focusManager.hasFocus
},isDirty:function(){return this.dirtyState||this.ckeditor.checkDirty()
},isValueChanged:function(){return this.valueChanged||this.isDirty()
},setReadOnly:function(F){this.ckeditor.setReadOnly(F!==false)
},isReadOnly:function(){return this.ckeditor.readOnly
},destroy:function(){window.clearInterval(this.dirtyCheckingInterval);
if(this.__getForm()){C.Event.unbind(this.__getForm(),"ajaxsubmit",this.__updateTextareaHandlerWrapper)
}if(this.ckeditor){this.ckeditor.destroy();
this.ckeditor=null
}this.__getTextarea().show();
E.destroy.call(this)
}})
})(RichFaces.jQuery,RichFaces);;/*
 * jQuery UI Draggable 1.11.2
 * http://jqueryui.com
 *
 * Copyright 2014 jQuery Foundation and other contributors
 * Released under the MIT license.
 * http://jquery.org/license
 *
 * http://api.jqueryui.com/draggable/
 */
(function(A){if(typeof define==="function"&&define.amd){define(["jquery","./core","./mouse","./widget"],A)
}else{A(jQuery)
}}(function(A){A.widget("ui.draggable",A.ui.mouse,{version:"1.11.2",widgetEventPrefix:"drag",options:{addClasses:true,appendTo:"parent",axis:false,connectToSortable:false,containment:false,cursor:"auto",cursorAt:false,grid:false,handle:false,helper:"original",iframeFix:false,opacity:false,refreshPositions:false,revert:false,revertDuration:500,scope:"default",scroll:true,scrollSensitivity:20,scrollSpeed:20,snap:false,snapMode:"both",snapTolerance:20,stack:false,zIndex:false,drag:null,start:null,stop:null},_create:function(){if(this.options.helper==="original"){this._setPositionRelative()
}if(this.options.addClasses){this.element.addClass("ui-draggable")
}if(this.options.disabled){this.element.addClass("ui-draggable-disabled")
}this._setHandleClassName();
this._mouseInit()
},_setOption:function(B,C){this._super(B,C);
if(B==="handle"){this._removeHandleClassName();
this._setHandleClassName()
}},_destroy:function(){if((this.helper||this.element).is(".ui-draggable-dragging")){this.destroyOnClear=true;
return 
}this.element.removeClass("ui-draggable ui-draggable-dragging ui-draggable-disabled");
this._removeHandleClassName();
this._mouseDestroy()
},_mouseCapture:function(B){var C=this.options;
this._blurActiveElement(B);
if(this.helper||C.disabled||A(B.target).closest(".ui-resizable-handle").length>0){return false
}this.handle=this._getHandle(B);
if(!this.handle){return false
}this._blockFrames(C.iframeFix===true?"iframe":C.iframeFix);
return true
},_blockFrames:function(B){this.iframeBlocks=this.document.find(B).map(function(){var C=A(this);
return A("<div>").css("position","absolute").appendTo(C.parent()).outerWidth(C.outerWidth()).outerHeight(C.outerHeight()).offset(C.offset())[0]
})
},_unblockFrames:function(){if(this.iframeBlocks){this.iframeBlocks.remove();
delete this.iframeBlocks
}},_blurActiveElement:function(D){var B=this.document[0];
if(!this.handleElement.is(D.target)){return 
}try{if(B.activeElement&&B.activeElement.nodeName.toLowerCase()!=="body"){A(B.activeElement).blur()
}}catch(C){}},_mouseStart:function(B){var C=this.options;
this.helper=this._createHelper(B);
this.helper.addClass("ui-draggable-dragging");
this._cacheHelperProportions();
if(A.ui.ddmanager){A.ui.ddmanager.current=this
}this._cacheMargins();
this.cssPosition=this.helper.css("position");
this.scrollParent=this.helper.scrollParent(true);
this.offsetParent=this.helper.offsetParent();
this.hasFixedAncestor=this.helper.parents().filter(function(){return A(this).css("position")==="fixed"
}).length>0;
this.positionAbs=this.element.offset();
this._refreshOffsets(B);
this.originalPosition=this.position=this._generatePosition(B,false);
this.originalPageX=B.pageX;
this.originalPageY=B.pageY;
(C.cursorAt&&this._adjustOffsetFromHelper(C.cursorAt));
this._setContainment();
if(this._trigger("start",B)===false){this._clear();
return false
}this._cacheHelperProportions();
if(A.ui.ddmanager&&!C.dropBehaviour){A.ui.ddmanager.prepareOffsets(this,B)
}this._normalizeRightBottom();
this._mouseDrag(B,true);
if(A.ui.ddmanager){A.ui.ddmanager.dragStart(this,B)
}return true
},_refreshOffsets:function(B){this.offset={top:this.positionAbs.top-this.margins.top,left:this.positionAbs.left-this.margins.left,scroll:false,parent:this._getParentOffset(),relative:this._getRelativeOffset()};
this.offset.click={left:B.pageX-this.offset.left,top:B.pageY-this.offset.top}
},_mouseDrag:function(B,D){if(this.hasFixedAncestor){this.offset.parent=this._getParentOffset()
}this.position=this._generatePosition(B,true);
this.positionAbs=this._convertPositionTo("absolute");
if(!D){var C=this._uiHash();
if(this._trigger("drag",B,C)===false){this._mouseUp({});
return false
}this.position=C.position
}this.helper[0].style.left=this.position.left+"px";
this.helper[0].style.top=this.position.top+"px";
if(A.ui.ddmanager){A.ui.ddmanager.drag(this,B)
}return false
},_mouseStop:function(C){var B=this,D=false;
if(A.ui.ddmanager&&!this.options.dropBehaviour){D=A.ui.ddmanager.drop(this,C)
}if(this.dropped){D=this.dropped;
this.dropped=false
}if((this.options.revert==="invalid"&&!D)||(this.options.revert==="valid"&&D)||this.options.revert===true||(A.isFunction(this.options.revert)&&this.options.revert.call(this.element,D))){A(this.helper).animate(this.originalPosition,parseInt(this.options.revertDuration,10),function(){if(B._trigger("stop",C)!==false){B._clear()
}})
}else{if(this._trigger("stop",C)!==false){this._clear()
}}return false
},_mouseUp:function(B){this._unblockFrames();
if(A.ui.ddmanager){A.ui.ddmanager.dragStop(this,B)
}if(this.handleElement.is(B.target)){this.element.focus()
}return A.ui.mouse.prototype._mouseUp.call(this,B)
},cancel:function(){if(this.helper.is(".ui-draggable-dragging")){this._mouseUp({})
}else{this._clear()
}return this
},_getHandle:function(B){return this.options.handle?!!A(B.target).closest(this.element.find(this.options.handle)).length:true
},_setHandleClassName:function(){this.handleElement=this.options.handle?this.element.find(this.options.handle):this.element;
this.handleElement.addClass("ui-draggable-handle")
},_removeHandleClassName:function(){this.handleElement.removeClass("ui-draggable-handle")
},_createHelper:function(C){var E=this.options,D=A.isFunction(E.helper),B=D?A(E.helper.apply(this.element[0],[C])):(E.helper==="clone"?this.element.clone().removeAttr("id"):this.element);
if(!B.parents("body").length){B.appendTo((E.appendTo==="parent"?this.element[0].parentNode:E.appendTo))
}if(D&&B[0]===this.element[0]){this._setPositionRelative()
}if(B[0]!==this.element[0]&&!(/(fixed|absolute)/).test(B.css("position"))){B.css("position","absolute")
}return B
},_setPositionRelative:function(){if(!(/^(?:r|a|f)/).test(this.element.css("position"))){this.element[0].style.position="relative"
}},_adjustOffsetFromHelper:function(B){if(typeof B==="string"){B=B.split(" ")
}if(A.isArray(B)){B={left:+B[0],top:+B[1]||0}
}if("left" in B){this.offset.click.left=B.left+this.margins.left
}if("right" in B){this.offset.click.left=this.helperProportions.width-B.right+this.margins.left
}if("top" in B){this.offset.click.top=B.top+this.margins.top
}if("bottom" in B){this.offset.click.top=this.helperProportions.height-B.bottom+this.margins.top
}},_isRootNode:function(B){return(/(html|body)/i).test(B.tagName)||B===this.document[0]
},_getParentOffset:function(){var C=this.offsetParent.offset(),B=this.document[0];
if(this.cssPosition==="absolute"&&this.scrollParent[0]!==B&&A.contains(this.scrollParent[0],this.offsetParent[0])){C.left+=this.scrollParent.scrollLeft();
C.top+=this.scrollParent.scrollTop()
}if(this._isRootNode(this.offsetParent[0])){C={top:0,left:0}
}return{top:C.top+(parseInt(this.offsetParent.css("borderTopWidth"),10)||0),left:C.left+(parseInt(this.offsetParent.css("borderLeftWidth"),10)||0)}
},_getRelativeOffset:function(){if(this.cssPosition!=="relative"){return{top:0,left:0}
}var B=this.element.position(),C=this._isRootNode(this.scrollParent[0]);
return{top:B.top-(parseInt(this.helper.css("top"),10)||0)+(!C?this.scrollParent.scrollTop():0),left:B.left-(parseInt(this.helper.css("left"),10)||0)+(!C?this.scrollParent.scrollLeft():0)}
},_cacheMargins:function(){this.margins={left:(parseInt(this.element.css("marginLeft"),10)||0),top:(parseInt(this.element.css("marginTop"),10)||0),right:(parseInt(this.element.css("marginRight"),10)||0),bottom:(parseInt(this.element.css("marginBottom"),10)||0)}
},_cacheHelperProportions:function(){this.helperProportions={width:this.helper.outerWidth(),height:this.helper.outerHeight()}
},_setContainment:function(){var C,F,D,E=this.options,B=this.document[0];
this.relativeContainer=null;
if(!E.containment){this.containment=null;
return 
}if(E.containment==="window"){this.containment=[A(window).scrollLeft()-this.offset.relative.left-this.offset.parent.left,A(window).scrollTop()-this.offset.relative.top-this.offset.parent.top,A(window).scrollLeft()+A(window).width()-this.helperProportions.width-this.margins.left,A(window).scrollTop()+(A(window).height()||B.body.parentNode.scrollHeight)-this.helperProportions.height-this.margins.top];
return 
}if(E.containment==="document"){this.containment=[0,0,A(B).width()-this.helperProportions.width-this.margins.left,(A(B).height()||B.body.parentNode.scrollHeight)-this.helperProportions.height-this.margins.top];
return 
}if(E.containment.constructor===Array){this.containment=E.containment;
return 
}if(E.containment==="parent"){E.containment=this.helper[0].parentNode
}F=A(E.containment);
D=F[0];
if(!D){return 
}C=/(scroll|auto)/.test(F.css("overflow"));
this.containment=[(parseInt(F.css("borderLeftWidth"),10)||0)+(parseInt(F.css("paddingLeft"),10)||0),(parseInt(F.css("borderTopWidth"),10)||0)+(parseInt(F.css("paddingTop"),10)||0),(C?Math.max(D.scrollWidth,D.offsetWidth):D.offsetWidth)-(parseInt(F.css("borderRightWidth"),10)||0)-(parseInt(F.css("paddingRight"),10)||0)-this.helperProportions.width-this.margins.left-this.margins.right,(C?Math.max(D.scrollHeight,D.offsetHeight):D.offsetHeight)-(parseInt(F.css("borderBottomWidth"),10)||0)-(parseInt(F.css("paddingBottom"),10)||0)-this.helperProportions.height-this.margins.top-this.margins.bottom];
this.relativeContainer=F
},_convertPositionTo:function(C,E){if(!E){E=this.position
}var B=C==="absolute"?1:-1,D=this._isRootNode(this.scrollParent[0]);
return{top:(E.top+this.offset.relative.top*B+this.offset.parent.top*B-((this.cssPosition==="fixed"?-this.offset.scroll.top:(D?0:this.offset.scroll.top))*B)),left:(E.left+this.offset.relative.left*B+this.offset.parent.left*B-((this.cssPosition==="fixed"?-this.offset.scroll.left:(D?0:this.offset.scroll.left))*B))}
},_generatePosition:function(C,I){var B,J,K,E,D=this.options,H=this._isRootNode(this.scrollParent[0]),G=C.pageX,F=C.pageY;
if(!H||!this.offset.scroll){this.offset.scroll={top:this.scrollParent.scrollTop(),left:this.scrollParent.scrollLeft()}
}if(I){if(this.containment){if(this.relativeContainer){J=this.relativeContainer.offset();
B=[this.containment[0]+J.left,this.containment[1]+J.top,this.containment[2]+J.left,this.containment[3]+J.top]
}else{B=this.containment
}if(C.pageX-this.offset.click.left<B[0]){G=B[0]+this.offset.click.left
}if(C.pageY-this.offset.click.top<B[1]){F=B[1]+this.offset.click.top
}if(C.pageX-this.offset.click.left>B[2]){G=B[2]+this.offset.click.left
}if(C.pageY-this.offset.click.top>B[3]){F=B[3]+this.offset.click.top
}}if(D.grid){K=D.grid[1]?this.originalPageY+Math.round((F-this.originalPageY)/D.grid[1])*D.grid[1]:this.originalPageY;
F=B?((K-this.offset.click.top>=B[1]||K-this.offset.click.top>B[3])?K:((K-this.offset.click.top>=B[1])?K-D.grid[1]:K+D.grid[1])):K;
E=D.grid[0]?this.originalPageX+Math.round((G-this.originalPageX)/D.grid[0])*D.grid[0]:this.originalPageX;
G=B?((E-this.offset.click.left>=B[0]||E-this.offset.click.left>B[2])?E:((E-this.offset.click.left>=B[0])?E-D.grid[0]:E+D.grid[0])):E
}if(D.axis==="y"){G=this.originalPageX
}if(D.axis==="x"){F=this.originalPageY
}}return{top:(F-this.offset.click.top-this.offset.relative.top-this.offset.parent.top+(this.cssPosition==="fixed"?-this.offset.scroll.top:(H?0:this.offset.scroll.top))),left:(G-this.offset.click.left-this.offset.relative.left-this.offset.parent.left+(this.cssPosition==="fixed"?-this.offset.scroll.left:(H?0:this.offset.scroll.left)))}
},_clear:function(){this.helper.removeClass("ui-draggable-dragging");
if(this.helper[0]!==this.element[0]&&!this.cancelHelperRemoval){this.helper.remove()
}this.helper=null;
this.cancelHelperRemoval=false;
if(this.destroyOnClear){this.destroy()
}},_normalizeRightBottom:function(){if(this.options.axis!=="y"&&this.helper.css("right")!=="auto"){this.helper.width(this.helper.width());
this.helper.css("right","auto")
}if(this.options.axis!=="x"&&this.helper.css("bottom")!=="auto"){this.helper.height(this.helper.height());
this.helper.css("bottom","auto")
}},_trigger:function(B,C,D){D=D||this._uiHash();
A.ui.plugin.call(this,B,[C,D,this],true);
if(/^(drag|start|stop)/.test(B)){this.positionAbs=this._convertPositionTo("absolute");
D.offset=this.positionAbs
}return A.Widget.prototype._trigger.call(this,B,C,D)
},plugins:{},_uiHash:function(){return{helper:this.helper,position:this.position,originalPosition:this.originalPosition,offset:this.positionAbs}
}});
A.ui.plugin.add("draggable","connectToSortable",{start:function(D,E,B){var C=A.extend({},E,{item:B.element});
B.sortables=[];
A(B.options.connectToSortable).each(function(){var F=A(this).sortable("instance");
if(F&&!F.options.disabled){B.sortables.push(F);
F.refreshPositions();
F._trigger("activate",D,C)
}})
},stop:function(D,E,B){var C=A.extend({},E,{item:B.element});
B.cancelHelperRemoval=false;
A.each(B.sortables,function(){var F=this;
if(F.isOver){F.isOver=0;
B.cancelHelperRemoval=true;
F.cancelHelperRemoval=false;
F._storedCSS={position:F.placeholder.css("position"),top:F.placeholder.css("top"),left:F.placeholder.css("left")};
F._mouseStop(D);
F.options.helper=F.options._helper
}else{F.cancelHelperRemoval=true;
F._trigger("deactivate",D,C)
}})
},drag:function(C,D,B){A.each(B.sortables,function(){var E=false,F=this;
F.positionAbs=B.positionAbs;
F.helperProportions=B.helperProportions;
F.offset.click=B.offset.click;
if(F._intersectsWith(F.containerCache)){E=true;
A.each(B.sortables,function(){this.positionAbs=B.positionAbs;
this.helperProportions=B.helperProportions;
this.offset.click=B.offset.click;
if(this!==F&&this._intersectsWith(this.containerCache)&&A.contains(F.element[0],this.element[0])){E=false
}return E
})
}if(E){if(!F.isOver){F.isOver=1;
F.currentItem=D.helper.appendTo(F.element).data("ui-sortable-item",true);
F.options._helper=F.options.helper;
F.options.helper=function(){return D.helper[0]
};
C.target=F.currentItem[0];
F._mouseCapture(C,true);
F._mouseStart(C,true,true);
F.offset.click.top=B.offset.click.top;
F.offset.click.left=B.offset.click.left;
F.offset.parent.left-=B.offset.parent.left-F.offset.parent.left;
F.offset.parent.top-=B.offset.parent.top-F.offset.parent.top;
B._trigger("toSortable",C);
B.dropped=F.element;
A.each(B.sortables,function(){this.refreshPositions()
});
B.currentItem=B.element;
F.fromOutside=B
}if(F.currentItem){F._mouseDrag(C);
D.position=F.position
}}else{if(F.isOver){F.isOver=0;
F.cancelHelperRemoval=true;
F.options._revert=F.options.revert;
F.options.revert=false;
F._trigger("out",C,F._uiHash(F));
F._mouseStop(C,true);
F.options.revert=F.options._revert;
F.options.helper=F.options._helper;
if(F.placeholder){F.placeholder.remove()
}B._refreshOffsets(C);
D.position=B._generatePosition(C,true);
B._trigger("fromSortable",C);
B.dropped=false;
A.each(B.sortables,function(){this.refreshPositions()
})
}}})
}});
A.ui.plugin.add("draggable","cursor",{start:function(D,E,B){var C=A("body"),F=B.options;
if(C.css("cursor")){F._cursor=C.css("cursor")
}C.css("cursor",F.cursor)
},stop:function(C,D,B){var E=B.options;
if(E._cursor){A("body").css("cursor",E._cursor)
}}});
A.ui.plugin.add("draggable","opacity",{start:function(D,E,B){var C=A(E.helper),F=B.options;
if(C.css("opacity")){F._opacity=C.css("opacity")
}C.css("opacity",F.opacity)
},stop:function(C,D,B){var E=B.options;
if(E._opacity){A(D.helper).css("opacity",E._opacity)
}}});
A.ui.plugin.add("draggable","scroll",{start:function(C,D,B){if(!B.scrollParentNotHidden){B.scrollParentNotHidden=B.helper.scrollParent(false)
}if(B.scrollParentNotHidden[0]!==B.document[0]&&B.scrollParentNotHidden[0].tagName!=="HTML"){B.overflowOffset=B.scrollParentNotHidden.offset()
}},drag:function(E,F,D){var G=D.options,C=false,H=D.scrollParentNotHidden[0],B=D.document[0];
if(H!==B&&H.tagName!=="HTML"){if(!G.axis||G.axis!=="x"){if((D.overflowOffset.top+H.offsetHeight)-E.pageY<G.scrollSensitivity){H.scrollTop=C=H.scrollTop+G.scrollSpeed
}else{if(E.pageY-D.overflowOffset.top<G.scrollSensitivity){H.scrollTop=C=H.scrollTop-G.scrollSpeed
}}}if(!G.axis||G.axis!=="y"){if((D.overflowOffset.left+H.offsetWidth)-E.pageX<G.scrollSensitivity){H.scrollLeft=C=H.scrollLeft+G.scrollSpeed
}else{if(E.pageX-D.overflowOffset.left<G.scrollSensitivity){H.scrollLeft=C=H.scrollLeft-G.scrollSpeed
}}}}else{if(!G.axis||G.axis!=="x"){if(E.pageY-A(B).scrollTop()<G.scrollSensitivity){C=A(B).scrollTop(A(B).scrollTop()-G.scrollSpeed)
}else{if(A(window).height()-(E.pageY-A(B).scrollTop())<G.scrollSensitivity){C=A(B).scrollTop(A(B).scrollTop()+G.scrollSpeed)
}}}if(!G.axis||G.axis!=="y"){if(E.pageX-A(B).scrollLeft()<G.scrollSensitivity){C=A(B).scrollLeft(A(B).scrollLeft()-G.scrollSpeed)
}else{if(A(window).width()-(E.pageX-A(B).scrollLeft())<G.scrollSensitivity){C=A(B).scrollLeft(A(B).scrollLeft()+G.scrollSpeed)
}}}}if(C!==false&&A.ui.ddmanager&&!G.dropBehaviour){A.ui.ddmanager.prepareOffsets(D,E)
}}});
A.ui.plugin.add("draggable","snap",{start:function(C,D,B){var E=B.options;
B.snapElements=[];
A(E.snap.constructor!==String?(E.snap.items||":data(ui-draggable)"):E.snap).each(function(){var G=A(this),F=G.offset();
if(this!==B.element[0]){B.snapElements.push({item:this,width:G.outerWidth(),height:G.outerHeight(),top:F.top,left:F.left})
}})
},drag:function(N,K,E){var B,S,G,H,M,J,I,T,O,F,L=E.options,R=L.snapTolerance,Q=K.offset.left,P=Q+E.helperProportions.width,D=K.offset.top,C=D+E.helperProportions.height;
for(O=E.snapElements.length-1;
O>=0;
O--){M=E.snapElements[O].left-E.margins.left;
J=M+E.snapElements[O].width;
I=E.snapElements[O].top-E.margins.top;
T=I+E.snapElements[O].height;
if(P<M-R||Q>J+R||C<I-R||D>T+R||!A.contains(E.snapElements[O].item.ownerDocument,E.snapElements[O].item)){if(E.snapElements[O].snapping){(E.options.snap.release&&E.options.snap.release.call(E.element,N,A.extend(E._uiHash(),{snapItem:E.snapElements[O].item})))
}E.snapElements[O].snapping=false;
continue
}if(L.snapMode!=="inner"){B=Math.abs(I-C)<=R;
S=Math.abs(T-D)<=R;
G=Math.abs(M-P)<=R;
H=Math.abs(J-Q)<=R;
if(B){K.position.top=E._convertPositionTo("relative",{top:I-E.helperProportions.height,left:0}).top
}if(S){K.position.top=E._convertPositionTo("relative",{top:T,left:0}).top
}if(G){K.position.left=E._convertPositionTo("relative",{top:0,left:M-E.helperProportions.width}).left
}if(H){K.position.left=E._convertPositionTo("relative",{top:0,left:J}).left
}}F=(B||S||G||H);
if(L.snapMode!=="outer"){B=Math.abs(I-D)<=R;
S=Math.abs(T-C)<=R;
G=Math.abs(M-Q)<=R;
H=Math.abs(J-P)<=R;
if(B){K.position.top=E._convertPositionTo("relative",{top:I,left:0}).top
}if(S){K.position.top=E._convertPositionTo("relative",{top:T-E.helperProportions.height,left:0}).top
}if(G){K.position.left=E._convertPositionTo("relative",{top:0,left:M}).left
}if(H){K.position.left=E._convertPositionTo("relative",{top:0,left:J-E.helperProportions.width}).left
}}if(!E.snapElements[O].snapping&&(B||S||G||H||F)){(E.options.snap.snap&&E.options.snap.snap.call(E.element,N,A.extend(E._uiHash(),{snapItem:E.snapElements[O].item})))
}E.snapElements[O].snapping=(B||S||G||H||F)
}}});
A.ui.plugin.add("draggable","stack",{start:function(D,E,B){var C,G=B.options,F=A.makeArray(A(G.stack)).sort(function(I,H){return(parseInt(A(I).css("zIndex"),10)||0)-(parseInt(A(H).css("zIndex"),10)||0)
});
if(!F.length){return 
}C=parseInt(A(F[0]).css("zIndex"),10)||0;
A(F).each(function(H){A(this).css("zIndex",C+H)
});
this.css("zIndex",(C+F.length))
}});
A.ui.plugin.add("draggable","zIndex",{start:function(D,E,B){var C=A(E.helper),F=B.options;
if(C.css("zIndex")){F._zIndex=C.css("zIndex")
}C.css("zIndex",F.zIndex)
},stop:function(C,D,B){var E=B.options;
if(E._zIndex){A(D.helper).css("zIndex",E._zIndex)
}}});
return A.ui.draggable
}));;var sbjQuery=jQuery;
sbjQuery.fn.SpinButton=function(A){return this.each(function(){this.spinCfg={min:A&&!isNaN(parseFloat(A.min))?Number(A.min):null,max:A&&!isNaN(parseFloat(A.max))?Number(A.max):null,step:A&&A.step?Number(A.step):1,page:A&&A.page?Number(A.page):10,upClass:A&&A.upClass?A.upClass:"up",downClass:A&&A.downClass?A.downClass:"down",reset:A&&A.reset?A.reset:this.value,delay:A&&A.delay?Number(A.delay):500,interval:A&&A.interval?Number(A.interval):100,_btn_width:20,_btn_height:12,_direction:null,_delay:null,_repeat:null,digits:A&&A.digits?Number(A.digits):1};
this.adjustValue=function(G){var F=this.value.toLowerCase();
if(F=="am"){this.value="PM";
return 
}else{if(F=="pm"){this.value="AM";
return 
}}F=(isNaN(this.value)?this.spinCfg.reset:Number(this.value))+Number(G);
if(this.spinCfg.min!==null){F=(F<this.spinCfg.min?(this.spinCfg.max!=null?this.spinCfg.max:this.spinCfg.min):F)
}if(this.spinCfg.max!==null){F=(F>this.spinCfg.max?(this.spinCfg.min!=null?this.spinCfg.min:this.spinCfg.max):F)
}var H=String(F);
while(H.length<this.spinCfg.digits){H="0"+H
}this.value=H
};
sbjQuery(this).keydown(function(F){switch(F.keyCode){case 38:this.adjustValue(this.spinCfg.step);
break;
case 40:this.adjustValue(-this.spinCfg.step);
break;
case 33:this.adjustValue(this.spinCfg.page);
break;
case 34:this.adjustValue(-this.spinCfg.page);
break
}}).bind("mousewheel",function(F){if(F.wheelDelta>=120){this.adjustValue(this.spinCfg.step)
}else{if(F.wheelDelta<=-120){this.adjustValue(-this.spinCfg.step)
}}F.preventDefault()
}).change(function(F){this.adjustValue(0)
});
var D=this;
var C=document.getElementById(this.id+"BtnUp");
sbjQuery(C).mousedown(function(G){var F=function(){D.adjustValue(D.spinCfg.step)
};
F();
D.spinCfg._delay=window.setTimeout(function(){F();
D.spinCfg._repeat=window.setInterval(F,D.spinCfg.interval)
},D.spinCfg.delay);
D.spinCfg._repeater=true;
return false
}).mouseup(function(F){D.spinCfg._repeater=false;
window.clearInterval(D.spinCfg._repeat);
window.clearTimeout(D.spinCfg._delay)
}).dblclick(function(F){if(RichFaces.browser.msie){D.adjustValue(D.spinCfg.step)
}}).mouseout(function(F){if(D.spinCfg._repeater){D.spinCfg._repeater=false;
window.clearInterval(D.spinCfg._repeat);
window.clearTimeout(D.spinCfg._delay)
}});
var E=document.getElementById(this.id+"BtnDown");
sbjQuery(E).mousedown(function(G){var F=function(){D.adjustValue(-D.spinCfg.step)
};
F();
D.spinCfg._delay=window.setTimeout(function(){F();
D.spinCfg._repeat=window.setInterval(F,D.spinCfg.interval)
},D.spinCfg.delay);
D.spinCfg._repeater=true;
return false
}).mouseup(function(F){D.spinCfg._repeater=false;
window.clearInterval(D.spinCfg._repeat);
window.clearTimeout(D.spinCfg._delay)
}).dblclick(function(F){if(RichFaces.browser.msie){D.adjustValue(-D.spinCfg.step)
}}).mouseout(function(F){if(D.spinCfg._repeater){D.spinCfg._repeater=false;
window.clearInterval(D.spinCfg._repeat);
window.clearTimeout(D.spinCfg._delay)
}});
if(this.addEventListener){this.addEventListener("DOMMouseScroll",function(F){if(F.detail>0){this.adjustValue(-this.spinCfg.step)
}else{if(F.detail<0){this.adjustValue(this.spinCfg.step)
}}F.preventDefault()
},false)
}});
function B(D,F){var E=D[F],C=document.body;
while((D=D.offsetParent)&&(D!=C)){if(!RichFaces.browser.msie||(D.currentStyle.position!="relative")){E+=D[F]
}}return E
}};;(function(C,B){B.ui=B.ui||{};
B.ui.InplaceBase=function(G,E){D.constructor.call(this,G);
var F=C.extend({},A,E);
this.editEvent=F.editEvent;
this.noneCss=F.noneCss;
this.changedCss=F.changedCss;
this.editCss=F.editCss;
this.defaultLabel=F.defaultLabel;
this.state=F.state;
this.options=F;
this.element=C(document.getElementById(G));
this.editContainer=C(document.getElementById(G+"Edit"));
this.element.bind(this.editEvent,C.proxy(this.__editHandler,this));
this.isSaved=false;
this.useDefaultLabel=false;
this.editState=false
};
B.ui.InputBase.extend(B.ui.InplaceBase);
var D=B.ui.InplaceBase.$super;
var A={editEvent:"click",state:"ready"};
C.extend(B.ui.InplaceBase.prototype,(function(){var E={READY:"ready",CHANGED:"changed",DISABLE:"disable",EDIT:"edit"};
return{getLabel:function(){},setLabel:function(F){},onshow:function(){},onhide:function(){},onsave:function(){},oncancel:function(){},save:function(){var F=this.__getValue();
if(F.length>0){this.setLabel(F);
this.useDefaultLabel=false
}else{this.setLabel(this.defaultLabel);
this.useDefaultLabel=true
}this.isSaved=true;
this.__applyChangedStyles();
this.onsave()
},cancel:function(){var F="";
if(!this.useDefaultLabel){F=this.getLabel()
}this.__setValue(F);
this.isSaved=true;
this.oncancel()
},isValueSaved:function(){return this.isSaved
},isEditState:function(){return this.editState
},__applyChangedStyles:function(){if(this.isValueChanged()){this.element.addClass(this.changedCss)
}else{this.element.removeClass(this.changedCss)
}},__show:function(){this.scrollElements=B.Event.bindScrollEventHandlers(this.id,this.__scrollHandler,this);
this.editState=true;
this.onshow()
},__hide:function(){if(this.scrollElements){B.Event.unbindScrollEventHandlers(this.scrollElements,this);
this.scrollElements=null
}this.editState=false;
this.editContainer.addClass(this.noneCss);
this.element.removeClass(this.editCss);
this.onhide()
},__editHandler:function(F){this.isSaved=false;
this.element.addClass(this.editCss);
this.editContainer.removeClass(this.noneCss);
this.__show()
},__scrollHandler:function(F){this.cancel()
},destroy:function(){D.destroy.call(this)
}}
})())
})(RichFaces.jQuery,window.RichFaces);;(function(C,B){B.ui=B.ui||{};
var A={position:"tr",direction:"vertical",method:"last",notifications:[],addNotification:function(E){this.notifications.push(E)
}};
B.ui.NotifyStack=B.BaseComponent.extendClass({name:"NotifyStack",init:function(F,E){D.constructor.call(this,F);
this.attachToDom(this.id);
this.__initializeStack(E)
},__initializeStack:function(G){var F=C.extend({},C.pnotify.defaults.pnotify_stack,A,G);
var H=(F.direction=="vertical");
var E=(F.method=="first");
F.push=E?"top":"bottom";
switch(F.position){case"tl":F.dir1=H?"down":"right";
F.dir2=H?"right":"down";
break;
case"tr":F.dir1=H?"down":"left";
F.dir2=H?"left":"down";
break;
case"bl":F.dir1=H?"up":"right";
F.dir2=H?"right":"up";
break;
case"br":F.dir1=H?"up":"left";
F.dir2=H?"left":"up";
break;
default:throw"wrong stack position: "+F.position
}this.stack=F
},getStack:function(){return this.stack
},removeNotifications:function(){var E;
while(E=this.stack.notifications.pop()){E.pnotify_remove()
}},destroy:function(){this.removeNotifications();
this.stack=null;
D.destroy.call(this)
}});
var D=B.ui.NotifyStack.$super
})(RichFaces.jQuery,RichFaces);;(function(D,T){T.ui=T.ui||{};
T.ui.Autocomplete=function(c,a,b){this.namespace="."+T.Event.createNamespace(this.name,c);
this.options={};
Y.constructor.call(this,c,c+K.SELECT,a,b);
this.attachToDom();
this.options=D.extend(this.options,X,b);
this.value="";
this.index=null;
this.isFirstAjax=true;
Q.call(this);
P.call(this);
M.call(this,"")
};
T.ui.AutocompleteBase.extend(T.ui.Autocomplete);
var Y=T.ui.Autocomplete.$super;
var X={itemClass:"rf-au-itm",selectedItemClass:"rf-au-itm-sel",subItemClass:"rf-au-opt",selectedSubItemClass:"rf-au-opt-sel",autofill:true,minChars:1,selectFirst:true,ajaxMode:true,lazyClientMode:false,isCachedAjax:true,tokens:"",attachToBody:true,filterFunction:undefined};
var K={SELECT:"List",ITEMS:"Items",VALUE:"Value"};
var A=/^[\n\s]*(.*)[\n\s]*$/;
var O=function(a){var b=[];
a.each(function(){b.push(D(this).text().replace(A,"$1"))
});
return b
};
var Q=function(){this.useTokens=(typeof this.options.tokens=="string"&&this.options.tokens.length>0);
if(this.useTokens){var a=this.options.tokens.split("").join("\\");
this.REGEXP_TOKEN_RIGHT=new RegExp("["+a+"]","i");
this.getLastTokenIndex=function(b){return RichFaces.ui.Autocomplete.__getLastTokenIndex(a,b)
}
}};
var P=function(){var a=D(T.getDomElement(this.id+K.ITEMS).parentNode);
a.on("click"+this.namespace,"."+this.options.itemClass,D.proxy(G,this));
a.on("mouseenter"+this.namespace,"."+this.options.itemClass,D.proxy(N,this))
};
var N=function(c){var b=D(c.target);
if(b&&!b.hasClass(this.options.itemClass)){b=b.parents("."+this.options.itemClass).get(0)
}if(b){var a=this.items.index(b);
F.call(this,c,a)
}};
var G=function(b){var a=D(b.target);
if(a&&!a.hasClass(this.options.itemClass)){a=a.parents("."+this.options.itemClass).get(0)
}if(a){this.__onEnter(b);
T.Selection.setCaretTo(T.getDomElement(this.fieldId));
this.__hide(b)
}};
var M=function(c,b){var d=D(T.getDomElement(this.id+K.ITEMS));
this.items=d.find("."+this.options.itemClass);
var a=d.data("componentData");
d.removeData("componentData");
if(this.items.length>0){this.cache=new T.utils.Cache((this.options.ajaxMode?c:""),this.items,b||a||O,!this.options.ajaxMode)
}};
var E=function(){var b=0;
this.items.slice(0,this.index).each(function(){b+=this.offsetHeight
});
var a=D(T.getDomElement(this.id+K.ITEMS)).parent();
if(b<a.scrollTop()){a.scrollTop(b)
}else{b+=this.items.eq(this.index).outerHeight();
if(b-a.scrollTop()>a.innerHeight()){a.scrollTop(b-a.innerHeight())
}}};
var R=function(a,c){if(this.options.autofill&&c.toLowerCase().indexOf(a)==0){var d=T.getDomElement(this.fieldId);
var e=T.Selection.getStart(d);
this.__setInputValue(a+c.substring(a.length));
var b=e+c.length-a.length;
T.Selection.set(d,e,b)
}};
var J=function(d,g){T.getDomElement(this.id+K.VALUE).value=this.value;
var f=this;
var a=d;
var c=function(h){M.call(f,f.value,h.componentData&&h.componentData[f.id]);
if(f.options.lazyClientMode&&f.value.length!=0){I.call(f,f.value)
}if(f.items.length!=0){if(g){(f.focused||f.isMouseDown)&&g.call(f,a)
}else{f.isVisible&&f.options.selectFirst&&F.call(f,a,0)
}}else{f.__hide(a)
}};
var b=function(h){f.__hide(a);
Z.call(f)
};
this.isFirstAjax=false;
var e={};
e[this.id+".ajax"]="1";
T.ajax(this.id,d,{parameters:e,error:b,complete:c})
};
var V=function(){if(this.index!=null){var a=this.items.eq(this.index);
if(a.removeClass(this.options.selectedItemClass).hasClass(this.options.subItemClass)){a.removeClass(this.options.selectedSubItemClass)
}this.index=null
}};
var F=function(d,a,c){if(this.items.length==0||(!c&&a==this.index)){return 
}if(a==null||a==undefined){V.call(this);
return 
}if(c){if(this.index==null){a=0
}else{a=this.index+a
}}if(a<0){a=0
}else{if(a>=this.items.length){a=this.items.length-1
}}if(a==this.index){return 
}V.call(this);
this.index=a;
var b=this.items.eq(this.index);
if(b.addClass(this.options.selectedItemClass).hasClass(this.options.subItemClass)){b.addClass(this.options.selectedSubItemClass)
}E.call(this);
if(d&&d.keyCode!=T.KEYS.BACKSPACE&&d.keyCode!=T.KEYS.DEL&&d.keyCode!=T.KEYS.LEFT&&d.keyCode!=T.KEYS.RIGHT){R.call(this,this.value,S.call(this))
}};
var I=function(b){var a=this.cache.getItems(b,this.options.filterFunction);
this.items=D(a);
D(T.getDomElement(this.id+K.ITEMS)).empty().append(this.items)
};
var Z=function(){D(T.getDomElement(this.id+K.ITEMS)).removeData().empty();
this.items=[]
};
var C=function(b,d,e){F.call(this,b);
var c=(typeof d=="undefined")?this.__getSubValue():d;
var a=this.value;
this.value=c;
if((this.options.isCachedAjax||!this.options.ajaxMode)&&this.cache&&this.cache.isCached(c)){if(a!=c){I.call(this,c)
}if(this.items.length!=0){e&&e.call(this,b)
}else{this.__hide(b)
}if(b.keyCode==T.KEYS.RETURN||b.type=="click"){this.__setInputValue(c)
}else{if(this.options.selectFirst){F.call(this,b,0)
}}}else{if(b.keyCode==T.KEYS.RETURN||b.type=="click"){this.__setInputValue(c)
}if(c.length>=this.options.minChars){if((this.options.ajaxMode||this.options.lazyClientMode)&&(a!=c||(a===""&&c===""))){J.call(this,b,e)
}}else{if(this.options.ajaxMode){Z.call(this);
this.__hide(b)
}}}};
var S=function(){if(this.index!=null){var a=this.items.eq(this.index);
return this.cache.getItemValue(a)
}return undefined
};
var W=function(){if(this.useTokens){var f=T.getDomElement(this.fieldId);
var e=f.value;
var b=T.Selection.getStart(f);
var c=e.substring(0,b);
var d=e.substring(b);
var a=c.substring(this.getLastTokenIndex(c));
r=d.search(this.REGEXP_TOKEN_RIGHT);
if(r==-1){r=d.length
}a+=d.substring(0,r);
return a
}else{return this.getValue()
}};
var H=function(a){var b=T.Selection.getStart(a);
if(b<=0){b=this.getLastTokenIndex(a.value)
}return b
};
var L=function(j){var i=T.getDomElement(this.fieldId);
var c=i.value;
var a=this.__getCursorPosition(i);
var e=c.substring(0,a);
var g=c.substring(a);
var h=this.getLastTokenIndex(e);
var f=h!=-1?h:e.length;
h=g.search(this.REGEXP_TOKEN_RIGHT);
var b=h!=-1?h:g.length;
var d=c.substring(0,f)+j;
a=d.length;
i.value=d+g.substring(b);
i.focus();
T.Selection.setCaretTo(i,a);
return i.value
};
var B=function(){if(this.items.length==0){return -1
}var d=D(T.getDomElement(this.id+K.ITEMS)).parent();
var b=d.scrollTop()+d.innerHeight()+this.items[0].offsetTop;
var c;
var a=(this.index!=null&&this.items[this.index].offsetTop<=b)?this.index:0;
for(a;
a<this.items.length;
a++){c=this.items[a];
if(c.offsetTop+c.offsetHeight>b){a--;
break
}}if(a!=this.items.length-1&&a==this.index){b+=this.items[a].offsetTop-d.scrollTop();
for(++a;
a<this.items.length;
a++){c=this.items[a];
if(c.offsetTop+c.offsetHeight>b){break
}}}return a
};
var U=function(){if(this.items.length==0){return -1
}var d=D(T.getDomElement(this.id+K.ITEMS)).parent();
var b=d.scrollTop()+this.items[0].offsetTop;
var c;
var a=(this.index!=null&&this.items[this.index].offsetTop>=b)?this.index-1:this.items.length-1;
for(a;
a>=0;
a--){c=this.items[a];
if(c.offsetTop<b){a++;
break
}}if(a!=0&&a==this.index){b=this.items[a].offsetTop-d.innerHeight();
if(b<this.items[0].offsetTop){b=this.items[0].offsetTop
}for(--a;
a>=0;
a--){c=this.items[a];
if(c.offsetTop<b){a++;
break
}}}return a
};
D.extend(T.ui.Autocomplete.prototype,(function(){return{name:"Autocomplete",__updateState:function(a){var b=this.__getSubValue();
if(this.items.length==0&&this.isFirstAjax){if((this.options.ajaxMode&&b.length>=this.options.minChars)||this.options.lazyClientMode){this.value=b;
J.call(this,a,this.__show);
return true
}}return false
},__getSubValue:W,__getCursorPosition:H,__updateInputValue:function(a){if(this.useTokens){return L.call(this,a)
}else{return Y.__updateInputValue.call(this,a)
}},__setInputValue:function(a){this.currentValue=this.__updateInputValue(a)
},__onChangeValue:C,__onKeyUp:function(a){F.call(this,a,-1,true)
},__onKeyDown:function(a){F.call(this,a,1,true)
},__onPageUp:function(a){F.call(this,a,U.call(this))
},__onPageDown:function(a){F.call(this,a,B.call(this))
},__onKeyHome:function(a){F.call(this,a,0)
},__onKeyEnd:function(a){F.call(this,a,this.items.length-1)
},__onBeforeShow:function(a){},__onEnter:function(a){var b=S.call(this);
this.__onChangeValue(a,b);
this.invokeEvent("selectitem",T.getDomElement(this.fieldId),a,b)
},__onShow:function(a){if(this.options.selectFirst){F.call(this,a,0)
}},__onHide:function(a){F.call(this,a)
},destroy:function(){this.items=null;
this.cache=null;
var a=T.getDomElement(this.id+K.ITEMS);
D(a).removeData();
T.Event.unbind(a.parentNode,this.namespace);
this.__conceal();
Y.destroy.call(this)
}}
})());
D.extend(T.ui.Autocomplete,{setData:function(b,a){D(T.getDomElement(b)).data("componentData",a)
},__getLastTokenIndex:function(g,f){var b=new RegExp("["+g+"][^"+g+"]*$","i");
var c=new RegExp("[^"+g+" ]","i");
var f=f||"";
var e=f.search(b);
if(e<0){return 0
}var a=f.substring(e);
var d=a.search(c);
if(d<=0){d=a.length
}return e+d
}})
})(RichFaces.jQuery,RichFaces);;(function(C,A){A.ui=A.ui||{};
var B={switchMode:"ajax"};
A.ui.CollapsiblePanelItem=A.ui.TogglePanelItem.extendClass({init:function(E,D){A.ui.TogglePanelItem.call(this,E,C.extend({},B,D));
this.headerClass="rf-cp-hdr-"+this.__state()
},__enter:function(){this.__content().show();
this.__header().addClass(this.headerClass);
return true
},__leave:function(){this.__content().hide();
if(this.options.switchMode=="client"){this.__header().removeClass(this.headerClass)
}return true
},__state:function(){return this.getName()==="true"?"exp":"colps"
},__content:function(){return C(A.getDomElement(this.id))
},__header:function(){return C(A.getDomElement(this.togglePanelId+":header"))
}})
})(RichFaces.jQuery,RichFaces);;(function(C,B){B.ui=B.ui||{};
B.ui.Popup=function(F,E){D.constructor.call(this,F);
this.options=C.extend({},A,E);
this.positionOptions={type:this.options.positionType,from:this.options.jointPoint,to:this.options.direction,offset:this.options.positionOffset};
this.popup=C(document.getElementById(F));
this.visible=this.options.visible;
this.attachTo=this.options.attachTo;
this.attachToBody=this.options.attachToBody;
this.positionType=this.options.positionType;
this.positionOffset=this.options.positionOffset
};
B.BaseComponent.extend(B.ui.Popup);
var D=B.ui.Popup.$super;
var A={visible:false};
C.extend(B.ui.Popup.prototype,{name:"popup",show:function(E){if(!this.visible){if(this.attachToBody){this.parentElement=this.popup.parent().get(0);
document.body.appendChild(this.popup.get(0))
}this.visible=true
}this.popup.setPosition(E||{id:this.attachTo},this.positionOptions).show()
},hide:function(){if(this.visible){this.popup.hide();
this.visible=false;
if(this.attachToBody&&this.parentElement){this.parentElement.appendChild(this.popup.get(0));
this.parentElement=null
}}},isVisible:function(){return this.visible
},getId:function(){return this.id
},destroy:function(){if(this.attachToBody&&this.parentElement){this.parentElement.appendChild(this.popup.get(0));
this.parentElement=null
}}})
})(RichFaces.jQuery,window.RichFaces);;/*
 * jQuery UI Droppable 1.11.2
 * http://jqueryui.com
 *
 * Copyright 2014 jQuery Foundation and other contributors
 * Released under the MIT license.
 * http://jquery.org/license
 *
 * http://api.jqueryui.com/droppable/
 */
(function(A){if(typeof define==="function"&&define.amd){define(["jquery","./core","./widget","./mouse","./draggable"],A)
}else{A(jQuery)
}}(function(A){A.widget("ui.droppable",{version:"1.11.2",widgetEventPrefix:"drop",options:{accept:"*",activeClass:false,addClasses:true,greedy:false,hoverClass:false,scope:"default",tolerance:"intersect",activate:null,deactivate:null,drop:null,out:null,over:null},_create:function(){var C,D=this.options,B=D.accept;
this.isover=false;
this.isout=true;
this.accept=A.isFunction(B)?B:function(E){return E.is(B)
};
this.proportions=function(){if(arguments.length){C=arguments[0]
}else{return C?C:C={width:this.element[0].offsetWidth,height:this.element[0].offsetHeight}
}};
this._addToManager(D.scope);
D.addClasses&&this.element.addClass("ui-droppable")
},_addToManager:function(B){A.ui.ddmanager.droppables[B]=A.ui.ddmanager.droppables[B]||[];
A.ui.ddmanager.droppables[B].push(this)
},_splice:function(B){var C=0;
for(;
C<B.length;
C++){if(B[C]===this){B.splice(C,1)
}}},_destroy:function(){var B=A.ui.ddmanager.droppables[this.options.scope];
this._splice(B);
this.element.removeClass("ui-droppable ui-droppable-disabled")
},_setOption:function(C,D){if(C==="accept"){this.accept=A.isFunction(D)?D:function(E){return E.is(D)
}
}else{if(C==="scope"){var B=A.ui.ddmanager.droppables[this.options.scope];
this._splice(B);
this._addToManager(D)
}}this._super(C,D)
},_activate:function(C){var B=A.ui.ddmanager.current;
if(this.options.activeClass){this.element.addClass(this.options.activeClass)
}if(B){this._trigger("activate",C,this.ui(B))
}},_deactivate:function(C){var B=A.ui.ddmanager.current;
if(this.options.activeClass){this.element.removeClass(this.options.activeClass)
}if(B){this._trigger("deactivate",C,this.ui(B))
}},_over:function(C){var B=A.ui.ddmanager.current;
if(!B||(B.currentItem||B.element)[0]===this.element[0]){return 
}if(this.accept.call(this.element[0],(B.currentItem||B.element))){if(this.options.hoverClass){this.element.addClass(this.options.hoverClass)
}this._trigger("over",C,this.ui(B))
}},_out:function(C){var B=A.ui.ddmanager.current;
if(!B||(B.currentItem||B.element)[0]===this.element[0]){return 
}if(this.accept.call(this.element[0],(B.currentItem||B.element))){if(this.options.hoverClass){this.element.removeClass(this.options.hoverClass)
}this._trigger("out",C,this.ui(B))
}},_drop:function(C,D){var B=D||A.ui.ddmanager.current,E=false;
if(!B||(B.currentItem||B.element)[0]===this.element[0]){return false
}this.element.find(":data(ui-droppable)").not(".ui-draggable-dragging").each(function(){var F=A(this).droppable("instance");
if(F.options.greedy&&!F.options.disabled&&F.options.scope===B.options.scope&&F.accept.call(F.element[0],(B.currentItem||B.element))&&A.ui.intersect(B,A.extend(F,{offset:F.element.offset()}),F.options.tolerance,C)){E=true;
return false
}});
if(E){return false
}if(this.accept.call(this.element[0],(B.currentItem||B.element))){if(this.options.activeClass){this.element.removeClass(this.options.activeClass)
}if(this.options.hoverClass){this.element.removeClass(this.options.hoverClass)
}this._trigger("drop",C,this.ui(B));
return this.element
}return false
},ui:function(B){return{draggable:(B.currentItem||B.element),helper:B.helper,position:B.position,offset:B.positionAbs}
}});
A.ui.intersect=(function(){function B(D,C,E){return(D>=C)&&(D<(C+E))
}return function(N,H,L,D){if(!H.offset){return false
}var F=(N.positionAbs||N.position.absolute).left+N.margins.left,K=(N.positionAbs||N.position.absolute).top+N.margins.top,E=F+N.helperProportions.width,J=K+N.helperProportions.height,G=H.offset.left,M=H.offset.top,C=G+H.proportions().width,I=M+H.proportions().height;
switch(L){case"fit":return(G<=F&&E<=C&&M<=K&&J<=I);
case"intersect":return(G<F+(N.helperProportions.width/2)&&E-(N.helperProportions.width/2)<C&&M<K+(N.helperProportions.height/2)&&J-(N.helperProportions.height/2)<I);
case"pointer":return B(D.pageY,M,H.proportions().height)&&B(D.pageX,G,H.proportions().width);
case"touch":return((K>=M&&K<=I)||(J>=M&&J<=I)||(K<M&&J>I))&&((F>=G&&F<=C)||(E>=G&&E<=C)||(F<G&&E>C));
default:return false
}}
})();
A.ui.ddmanager={current:null,droppables:{"default":[]},prepareOffsets:function(E,G){var D,C,B=A.ui.ddmanager.droppables[E.options.scope]||[],F=G?G.type:null,H=(E.currentItem||E.element).find(":data(ui-droppable)").addBack();
droppablesLoop:for(D=0;
D<B.length;
D++){if(B[D].options.disabled||(E&&!B[D].accept.call(B[D].element[0],(E.currentItem||E.element)))){continue
}for(C=0;
C<H.length;
C++){if(H[C]===B[D].element[0]){B[D].proportions().height=0;
continue droppablesLoop
}}B[D].visible=B[D].element.css("display")!=="none";
if(!B[D].visible){continue
}if(F==="mousedown"){B[D]._activate.call(B[D],G)
}B[D].offset=B[D].element.offset();
B[D].proportions({width:B[D].element[0].offsetWidth,height:B[D].element[0].offsetHeight})
}},drop:function(B,C){var D=false;
A.each((A.ui.ddmanager.droppables[B.options.scope]||[]).slice(),function(){if(!this.options){return 
}if(!this.options.disabled&&this.visible&&A.ui.intersect(B,this,this.options.tolerance,C)){D=this._drop.call(this,C)||D
}if(!this.options.disabled&&this.visible&&this.accept.call(this.element[0],(B.currentItem||B.element))){this.isout=true;
this.isover=false;
this._deactivate.call(this,C)
}});
return D
},dragStart:function(B,C){B.element.parentsUntil("body").bind("scroll.droppable",function(){if(!B.options.refreshPositions){A.ui.ddmanager.prepareOffsets(B,C)
}})
},drag:function(B,C){if(B.options.refreshPositions){A.ui.ddmanager.prepareOffsets(B,C)
}A.each(A.ui.ddmanager.droppables[B.options.scope]||[],function(){if(this.options.disabled||this.greedyChild||!this.visible){return 
}var G,E,D,F=A.ui.intersect(B,this,this.options.tolerance,C),H=!F&&this.isover?"isout":(F&&!this.isover?"isover":null);
if(!H){return 
}if(this.options.greedy){E=this.options.scope;
D=this.element.parents(":data(ui-droppable)").filter(function(){return A(this).droppable("instance").options.scope===E
});
if(D.length){G=A(D[0]).droppable("instance");
G.greedyChild=(H==="isover")
}}if(G&&H==="isover"){G.isover=false;
G.isout=true;
G._out.call(G,C)
}this[H]=true;
this[H==="isout"?"isover":"isout"]=false;
this[H==="isover"?"_over":"_out"].call(this,C);
if(G&&H==="isout"){G.isout=false;
G.isover=true;
G._over.call(G,C)
}})
},dragStop:function(B,C){B.element.parentsUntil("body").unbind("scroll.droppable");
if(!B.options.refreshPositions){A.ui.ddmanager.prepareOffsets(B,C)
}}};
return A.ui.droppable
}));;(function($,rf){rf.calendarUtils=rf.calendarUtils||{};
var getDefaultMonthNames=function(shortNames){return(shortNames?["Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"]:["January","February","March","April","May","June","July","August","September","October","November","December"])
};
$.extend(rf.calendarUtils,{joinArray:function(array,begin,end,separator){var value="";
if(array.length!=0){value=begin+array.pop()+end
}while(array.length){value=begin+array.pop()+end+separator+value
}return value
},getMonthByLabel:function(monthLabel,monthNames){var toLowerMonthLabel=monthLabel.toLowerCase();
var i=0;
while(i<monthNames.length){if(monthNames[i].toLowerCase()==toLowerMonthLabel){return i
}i++
}},createDate:function(yy,mm,dd,h,m,s){h=h||0;
m=m||0;
s=s||0;
var date=new Date(yy,mm,dd,h,m,s);
if(date.getDate()!=dd){date=new Date(yy,mm);
date.setHours(h);
date.setMinutes(m);
date.setSeconds(s);
date.setUTCDate(dd)
}return date
},parseDate:function(dateString,pattern,monthNames,monthNamesShort){var re=/([.*+?^<>=!:${}()\[\]\/\\])/g;
var monthNamesStr;
var monthNamesShortStr;
if(!monthNames){monthNames=getDefaultMonthNames();
monthNamesStr=monthNames.join("|")
}else{monthNamesStr=monthNames.join("|").replace(re,"\\$1")
}if(!monthNamesShort){monthNamesShort=getDefaultMonthNames(true);
monthNamesShortStr=monthNamesShort.join("|")
}else{monthNamesShortStr=monthNamesShort.join("|").replace(re,"\\$1")
}var counter=1;
var y,m,d;
var a,h,min,s;
var shortLabel=false;
pattern=pattern.replace(/([.*+?^<>=!:${}()|\[\]\/\\])/g,"\\$1");
pattern=pattern.replace(/(y+|M+|d+|a|H{1,2}|h{1,2}|m{2}|s{2})/g,function($1){switch($1){case"y":case"yy":y=counter;
counter++;
return"(\\d{2})";
case"MM":m=counter;
counter++;
return"(\\d{2})";
case"M":m=counter;
counter++;
return"(\\d{1,2})";
case"d":d=counter;
counter++;
return"(\\d{1,2})";
case"MMM":m=counter;
counter++;
shortLabel=true;
return"("+monthNamesShortStr+")";
case"a":a=counter;
counter++;
return"(AM|am|PM|pm)?";
case"HH":case"hh":h=counter;
counter++;
return"(\\d{2})?";
case"H":case"h":h=counter;
counter++;
return"(\\d{1,2})?";
case"mm":min=counter;
counter++;
return"(\\d{2})?";
case"ss":s=counter;
counter++;
return"(\\d{2})?"
}var ch=$1.charAt(0);
if(ch=="y"){y=counter;
counter++;
return"(\\d{3,4})"
}if(ch=="M"){m=counter;
counter++;
return"("+monthNamesStr+")"
}if(ch=="d"){d=counter;
counter++;
return"(\\d{2})"
}});
var re=new RegExp(pattern,"i");
var match=dateString.match(re);
if(match!=null&&y!=undefined&&m!=undefined){var correctYear=false;
var defaultCenturyStart=new Date();
defaultCenturyStart.setFullYear(defaultCenturyStart.getFullYear()-80);
var yy=parseInt(match[y],10);
if(isNaN(yy)){return null
}else{if(yy<100){var defaultCenturyStartYear=defaultCenturyStart.getFullYear();
var ambiguousTwoDigitYear=defaultCenturyStartYear%100;
correctYear=yy==ambiguousTwoDigitYear;
yy+=Math.floor(defaultCenturyStartYear/100)*100+(yy<ambiguousTwoDigitYear?100:0)
}}var mm=parseInt(match[m],10);
if(isNaN(mm)){mm=this.getMonthByLabel(match[m],shortLabel?monthNamesShort:monthNames)
}else{if(--mm<0||mm>11){return null
}}var addDay=correctYear?1:0;
var dd;
if(d!=undefined){dd=parseInt(match[d],10)
}else{dd=1
}if(isNaN(dd)||dd<1||dd>this.daysInMonth(yy,mm)+addDay){return null
}var date;
if(min!=undefined&&h!=undefined){var hh,mmin,aa;
mmin=parseInt(match[min],10);
if(isNaN(mmin)||mmin<0||mmin>59){return null
}hh=parseInt(match[h],10);
if(isNaN(hh)){return null
}if(a!=undefined){aa=match[a];
if(!aa){return null
}aa=aa.toLowerCase();
if((aa!="am"&&aa!="pm")||hh<1||hh>12){return null
}if(aa=="pm"){if(hh!=12){hh+=12
}}else{if(hh==12){hh=0
}}}else{if(hh<0||hh>23){return null
}}date=this.createDate(yy,mm,dd,hh,mmin);
if(s!=undefined){sec=parseInt(match[s],10);
if(isNaN(sec)||sec<0||sec>59){return null
}date.setSeconds(sec)
}}else{date=this.createDate(yy,mm,dd)
}if(correctYear){if(date.getTime()<defaultCenturyStart.getTime()){date.setFullYear(yy+100)
}if(date.getMonth()!=mm){return null
}}return date
}return null
},formatDate:function(date,pattern,monthNames,monthNamesShort){if(!monthNames){monthNames=getDefaultMonthNames()
}if(!monthNamesShort){monthNamesShort=getDefaultMonthNames(true)
}var mm,dd,hh,min,sec;
var result=pattern.replace(/(\\\\|\\[yMdaHhms])|(y+|M+|d+|a|H{1,2}|h{1,2}|m{2}|s{2})/g,function($1,$2,$3){if($2){return $2.charAt(1)
}switch($3){case"y":case"yy":return date.getYear().toString().slice(-2);
case"M":return(date.getMonth()+1);
case"MM":return((mm=date.getMonth()+1)<10?"0"+mm:mm);
case"MMM":return monthNamesShort[date.getMonth()];
case"d":return date.getDate();
case"a":return(date.getHours()<12?"AM":"PM");
case"HH":return((hh=date.getHours())<10?"0"+hh:hh);
case"H":return date.getHours();
case"hh":return((hh=date.getHours())==0?"12":(hh<10?"0"+hh:(hh>21?hh-12:(hh>12)?"0"+(hh-12):hh)));
case"h":return((hh=date.getHours())==0?"12":(hh>12?hh-12:hh));
case"mm":return((min=date.getMinutes())<10?"0"+min:min);
case"ss":return((sec=date.getSeconds())<10?"0"+sec:sec)
}var ch=$3.charAt(0);
if(ch=="y"){return date.getFullYear()
}if(ch=="M"){return monthNames[date.getMonth()]
}if(ch=="d"){return((dd=date.getDate())<10?"0"+dd:dd)
}});
return result
},isLeapYear:function(year){return new Date(year,1,29).getDate()==29
},daysInMonth:function(year,month){return 32-new Date(year,month,32).getDate()
},daysInMonthByDate:function(date){return 32-new Date(date.getFullYear(),date.getMonth(),32).getDate()
},getDay:function(date,firstWeekDay){var value=date.getDay()-firstWeekDay;
if(value<0){value=7+value
}return value
},getFirstWeek:function(year,mdifw,fdow){var date=new Date(year,0,1);
var firstday=this.getDay(date,fdow);
var weeknumber=(7-firstday<mdifw)?0:1;
return{date:date,firstDay:firstday,weekNumber:weeknumber,mdifw:mdifw,fdow:fdow}
},getLastWeekOfPrevYear:function(o){var year=o.date.getFullYear()-1;
var days=(this.isLeapYear(year)?366:365);
var obj=this.getFirstWeek(year,o.mdifw,o.fdow);
days=(days-7+o.firstDay);
var weeks=Math.ceil(days/7);
return weeks+obj.weekNumber
},weekNumber:function(year,month,mdifw,fdow){var o=this.getFirstWeek(year,mdifw,fdow);
if(month==0){if(o.weekNumber==1){return 1
}return this.getLastWeekOfPrevYear(o)
}var oneweek=604800000;
var d=new Date(year,month,1);
d.setDate(1+o.firstDay+(this.getDay(d,fdow)==0?1:0));
weeknumber=o.weekNumber+Math.floor((d.getTime()-o.date.getTime())/oneweek);
return weeknumber
}});
rf.calendarTemplates=rf.calendarTemplates||{};
$.extend(rf.calendarTemplates,(function(){var VARIABLE_NAME_PATTERN=/^\s*[_,A-Z,a-z][\w,_\.]*\s*$/;
var getObjectValue=function(str,object){var a=str.split(".");
var value=object[a[0]];
var c=1;
while(value&&c<a.length){value=value[a[c++]]
}return(value?value:"")
};
return{evalMacro:function(template,object){var _value_="";
if(VARIABLE_NAME_PATTERN.test(template)){if(template.indexOf(".")==-1){_value_=object[template];
if(!_value_){_value_=window[template]
}}else{_value_=getObjectValue(template,object);
if(!_value_){_value_=getObjectValue(template,window)
}}if(_value_&&typeof _value_=="function"){_value_=_value_(object)
}if(!_value_){_value_=""
}}else{try{if(object.eval){_value_=object.eval(template)
}else{with(object){_value_=eval(template)
}}if(typeof _value_=="function"){_value_=_value_(object)
}}catch(e){LOG.warn("Exception: "+e.Message+"\n["+template+"]")
}}return _value_
}}
})())
})(RichFaces.jQuery,RichFaces);;(function(F,D){D.ui=D.ui||{};
D.ui.List=function(J,H){G.constructor.call(this,J);
this.namespace=this.namespace||"."+D.Event.createNamespace(this.name,this.id);
this.attachToDom();
var I=F.extend({},A,H);
this.list=F(document.getElementById(J));
this.selectListener=I.selectListener;
this.selectItemCss=I.selectItemCss;
this.selectItemCssMarker=I.selectItemCss.split(" ",1)[0];
this.scrollContainer=F(I.scrollContainer);
this.itemCss=I.itemCss.split(" ",1)[0];
this.listCss=I.listCss;
this.clickRequiredToSelect=I.clickRequiredToSelect;
this.index=-1;
this.disabled=I.disabled;
this.focusKeeper=F(document.getElementById(J+"FocusKeeper"));
this.focusKeeper.focused=false;
this.isMouseDown=false;
this.list.bind("mousedown",F.proxy(this.__onMouseDown,this)).bind("mouseup",F.proxy(this.__onMouseUp,this));
B.call(this);
if(I.focusKeeperEnabled){C.call(this)
}this.__updateItemsList();
if(I.clientSelectItems!==null){this.__storeClientSelectItems(I.clientSelectItems)
}};
D.BaseComponent.extend(D.ui.List);
var G=D.ui.List.$super;
var A={clickRequiredToSelect:false,disabled:false,selectListener:false,clientSelectItems:null,focusKeeperEnabled:true};
var B=function(){var H={};
H["click"+this.namespace]=F.proxy(this.onClick,this);
H["dblclick"+this.namespace]=F.proxy(this.onDblclick,this);
this.list.on("mouseover"+this.namespace,"."+this.itemCss,F.proxy(E,this));
D.Event.bind(this.list,H,this)
};
var C=function(){var H={};
H["keydown"+this.namespace]=F.proxy(this.__keydownHandler,this);
H["blur"+this.namespace]=F.proxy(this.__blurHandler,this);
H["focus"+this.namespace]=F.proxy(this.__focusHandler,this);
D.Event.bind(this.focusKeeper,H,this)
};
var E=function(I){var H=F(I.target);
if(H&&!this.clickRequiredToSelect&&!this.disabled){this.__select(H)
}};
F.extend(D.ui.List.prototype,(function(){return{name:"list",processItem:function(H){if(this.selectListener.processItem&&typeof this.selectListener.processItem=="function"){this.selectListener.processItem(H)
}},isSelected:function(H){return H.hasClass(this.selectItemCssMarker)
},selectItem:function(H){if(this.selectListener.selectItem&&typeof this.selectListener.selectItem=="function"){this.selectListener.selectItem(H)
}else{H.addClass(this.selectItemCss);
D.Event.fire(this,"selectItem",H)
}this.__scrollToSelectedItem(this)
},unselectItem:function(H){if(this.selectListener.unselectItem&&typeof this.selectListener.unselectItem=="function"){this.selectListener.unselectItem(H)
}else{H.removeClass(this.selectItemCss);
D.Event.fire(this,"unselectItem",H)
}},__focusHandler:function(H){if(!this.focusKeeper.focused){this.focusKeeper.focused=true;
D.Event.fire(this,"listfocus"+this.namespace,H)
}},__blurHandler:function(I){if(!this.isMouseDown){var H=this;
this.timeoutId=window.setTimeout(function(){H.focusKeeper.focused=false;
H.invokeEvent.call(H,"blur",document.getElementById(H.id),I);
D.Event.fire(H,"listblur"+H.namespace,I)
},200)
}else{this.isMouseDown=false
}},__onMouseDown:function(H){this.isMouseDown=true
},__onMouseUp:function(H){this.isMouseDown=false
},__keydownHandler:function(I){if(I.isDefaultPrevented()){return 
}if(I.metaKey||I.ctrlKey){return 
}var H;
if(I.keyCode){H=I.keyCode
}else{if(I.which){H=I.which
}}switch(H){case D.KEYS.DOWN:I.preventDefault();
this.__selectNext();
break;
case D.KEYS.UP:I.preventDefault();
this.__selectPrev();
break;
case D.KEYS.HOME:I.preventDefault();
this.__selectByIndex(0);
break;
case D.KEYS.END:I.preventDefault();
this.__selectByIndex(this.items.length-1);
break;
default:break
}},onClick:function(I){this.setFocus();
var H=this.__getItem(I);
if(!H){return 
}this.processItem(H);
var J=I.metaKey||I.ctrlKey;
if(!this.disabled){this.__select(H,J&&this.clickRequiredToSelect)
}},onDblclick:function(I){this.setFocus();
var H=this.__getItem(I);
if(!H){return 
}this.processItem(H);
if(!this.disabled){this.__select(H,false)
}},currentSelectItem:function(){if(this.items&&this.index!=-1){return F(this.items[this.index])
}},getSelectedItemIndex:function(){return this.index
},removeItems:function(H){F(H).detach();
this.__updateItemsList();
D.Event.fire(this,"removeitems",H)
},removeAllItems:function(){var H=this.__getItems();
this.removeItems(H);
return H
},addItems:function(H){var I=this.scrollContainer;
I.append(H);
this.__updateItemsList();
D.Event.fire(this,"additems",H)
},move:function(H,J){if(J===0){return 
}var I=this;
if(J>0){H=F(H.get().reverse())
}H.each(function(M){var L=I.items.index(this);
var K=L+J;
var N=I.items[K];
if(J<0){F(this).insertBefore(N)
}else{F(this).insertAfter(N)
}I.index=I.index+J;
I.__updateItemsList()
});
D.Event.fire(this,"moveitems",H)
},getItemByIndex:function(H){if(H>=0&&H<this.items.length){return this.items[H]
}},getClientSelectItemByIndex:function(H){if(H>=0&&H<this.items.length){return F(this.items[H]).data("clientSelectItem")
}},resetSelection:function(){var H=this.currentSelectItem();
if(H){this.unselectItem(F(H))
}this.index=-1
},isList:function(H){var I=H.parents("."+this.listCss).attr("id");
return(I&&(I==this.getId()))
},length:function(){return this.items.length
},__updateIndex:function(I){if(I===null){this.index=-1
}else{var H=this.items.index(I);
if(H<0){H=0
}else{if(H>=this.items.length){H=this.items.length-1
}}this.index=H
}},__updateItemsList:function(){return(this.items=this.list.find("."+this.itemCss))
},__storeClientSelectItems:function(H){var I=[];
F.each(H,function(J){I[this.id]=this
});
this.items.each(function(J){var K=F(this);
var M=K.attr("id");
var L=I[M];
K.data("clientSelectItem",L)
})
},__select:function(I,J){var H=this.items.index(I);
this.__selectByIndex(H,J)
},__selectByIndex:function(H,J){if(!this.__isSelectByIndexValid(H)){return 
}if(!this.clickRequiredToSelect&&this.index==H){return 
}var K=this.__unselectPrevious();
if(this.clickRequiredToSelect&&K==H){return 
}this.index=this.__sanitizeSelectedIndex(H);
var I=this.items.eq(this.index);
if(this.isSelected(I)){this.unselectItem(I)
}else{this.selectItem(I)
}},__isSelectByIndexValid:function(H){if(this.items.length==0){return false
}if(H==undefined){this.index=-1;
return false
}return true
},__sanitizeSelectedIndex:function(I){var H;
if(I<0){H=0
}else{if(I>=this.items.length){H=this.items.length-1
}else{H=I
}}return H
},__unselectPrevious:function(){var I=this.index;
if(I!=-1){var H=this.items.eq(I);
this.unselectItem(H);
this.index=-1
}return I
},__selectItemByValue:function(J){var I=null;
this.resetSelection();
var H=this;
this.__getItems().each(function(K){if(F(this).data("clientSelectItem").value==J){H.__selectByIndex(K);
I=F(this);
return false
}});
return I
},csvEncodeValues:function(){var H=new Array();
this.__getItems().each(function(I){H.push(F(this).data("clientSelectItem").value)
});
return H.join(",")
},__selectCurrent:function(){var H;
if(this.items&&this.index>=0){H=this.items.eq(this.index);
this.processItem(H)
}},__getAdjacentIndex:function(I){var H=this.index+I;
if(H<0){H=this.items.length-1
}else{if(H>=this.items.length){H=0
}}return H
},__selectPrev:function(){this.__selectByIndex(this.__getAdjacentIndex(-1))
},__selectNext:function(){this.__selectByIndex(this.__getAdjacentIndex(1))
},__getItem:function(H){return F(H.target).closest("."+this.itemCss,H.currentTarget).get(0)
},__getItems:function(){return this.items
},__setItems:function(H){this.items=H
},__scrollToSelectedItem:function(){if(this.scrollContainer){var H=this.scrollContainer[0].getBoundingClientRect(),J=this.items.get(this.index).getBoundingClientRect();
if(H.top<J.top&&J.bottom<H.bottom){return 
}var L=J.top,I=H.top,K=this.scrollContainer.scrollTop()+L-I;
this.scrollContainer.scrollTop(K)
}},setFocus:function(){this.focusKeeper.focus()
}}
})())
})(RichFaces.jQuery,window.RichFaces);;(function(C,B){B.ui=B.ui||{};
var E={rejectClass:"rf-ind-rejt",acceptClass:"rf-ind-acpt",draggingClass:"rf-ind-drag"};
B.ui.Draggable=function(I,F){this.options={};
C.extend(this.options,A,F||{});
D.constructor.call(this,I);
this.id=I;
this.namespace=this.namespace||"."+B.Event.createNamespace(this.name,this.id);
this.parentId=this.options.parentId;
this.attachToDom(this.parentId);
this.dragElement=C(document.getElementById(this.options.parentId));
this.dragElement.draggable();
if(F.indicator){var G=C(document.getElementById(F.indicator));
var H=G.clone();
C("*[id]",H).andSelf().each(function(){C(this).removeAttr("id")
});
if(G.attr("id")){H.attr("id",G.attr("id")+"Clone")
}this.dragElement.data("indicator",true);
this.dragElement.draggable("option","helper",function(){return H
})
}else{this.dragElement.data("indicator",false);
this.dragElement.draggable("option","helper","clone")
}this.dragElement.draggable("option","addClasses",false);
this.dragElement.draggable("option","appendTo","body");
this.dragElement.data("type",this.options.type);
this.dragElement.data("init",true);
this.dragElement.data("id",this.id);
B.Event.bind(this.dragElement,"dragstart"+this.namespace,this.dragStart,this);
B.Event.bind(this.dragElement,"drag"+this.namespace,this.drag,this)
};
B.BaseNonVisualComponent.extend(B.ui.Draggable);
var D=B.ui.Draggable.$super;
var A={};
C.extend(B.ui.Draggable.prototype,(function(){return{name:"Draggable",dragStart:function(J){var G=J.rf.data;
var F=G.helper[0];
this.parentElement=F.parentNode;
if(this.__isCustomDragIndicator()){G.helper.detach().appendTo("body").show();
var I=(G.helper.width()/2);
var H=(G.helper.height()/2);
this.dragElement.data("ui-draggable").offset.click.left=I;
this.dragElement.data("ui-draggable").offset.click.top=H
}},drag:function(H){var G=H.rf.data;
if(this.__isCustomDragIndicator()){var F=B.component(this.options.indicator);
if(F){G.helper.addClass(F.getDraggingClass())
}else{G.helper.addClass(E.draggingClass)
}}this.__clearDraggableCss(G.helper)
},__isCustomDragIndicator:function(){return this.dragElement.data("indicator")
},__clearDraggableCss:function(F){if(F&&F.removeClass){F.removeClass("ui-draggable-dragging")
}},destroy:function(){this.detach(this.parentId);
B.Event.unbind(this.dragElement,this.namespace);
D.destroy.call(this)
}}
})())
})(RichFaces.jQuery,window.RichFaces);;(function(F,L){L.ui=L.ui||{};
var B={getControl:function(Q,N,O,P){var M=F.extend({onclick:(O?"RichFaces.$$('Calendar',this)."+O+"("+(P?P:"")+");":"")+"return true;"},N);
return new E("div",M,[new T(Q)])
},getSelectedDateControl:function(O){if(!O.selectedDate||O.options.showApplyButton){return""
}var P=L.calendarUtils.formatDate(O.selectedDate,(O.timeType?O.datePattern:O.options.datePattern),O.options.monthLabels,O.options.monthLabelsShort);
var N="RichFaces.$$('Calendar',this).showSelectedDate(); return true;";
var M=(O.options.disabled?new E("div",{"class":"rf-cal-tl-btn-dis"},[new ET(P)]):new E("div",{"class":"rf-cal-tl-btn",onclick:N},[new ET(P)]));
return M
},getTimeControl:function(P){if(!P.selectedDate||!P.timeType){return""
}var R=L.calendarUtils.formatDate(P.selectedDate,P.timePattern,P.options.monthLabels,P.options.monthLabelsShort);
var Q="RichFaces.jQuery(this).removeClass('rf-cal-btn-press');";
var O="RichFaces.jQuery(this).addClass('rf-cal-btn-press');";
var N="RichFaces.$$('Calendar',this).showTimeEditor();return true;";
var M=P.options.disabled||P.options.readonly?new E("div",{"class":"rf-cal-tl-btn-btn-dis"},[new ET(R)]):new E("div",{"class":"rf-cal-tl-btn rf-cal-tl-btn-hov rf-cal-btn-press",onclick:N,onmouseover:+Q,onmouseout:+O},[new ET(R)]);
return M
},toolButtonAttributes:{className:"rf-cal-tl-btn",onmouseover:"this.className='rf-cal-tl-btn rf-cal-tl-btn-hov'",onmouseout:"this.className='rf-cal-tl-btn'",onmousedown:"this.className='rf-cal-tl-btn rf-cal-tl-btn-hov rf-cal-tl-btn-btn-press'",onmouseup:"this.className='rf-cal-tl-btn rf-cal-tl-btn-hov'"},nextYearControl:function(M){return(!M.calendar.options.disabled?B.getControl(">>",B.toolButtonAttributes,"nextYear"):"")
},previousYearControl:function(M){return(!M.calendar.options.disabled?B.getControl("<<",B.toolButtonAttributes,"prevYear"):"")
},nextMonthControl:function(M){return(!M.calendar.options.disabled?B.getControl(">",B.toolButtonAttributes,"nextMonth"):"")
},previousMonthControl:function(M){return(!M.calendar.options.disabled?B.getControl("<",B.toolButtonAttributes,"prevMonth"):"")
},currentMonthControl:function(N){var O=L.calendarUtils.formatDate(N.calendar.getCurrentDate(),"MMMM, yyyy",N.monthLabels,N.monthLabelsShort);
var M=N.calendar.options.disabled?new E("div",{className:"rf-cal-tl-btn-dis"},[new T(O)]):B.getControl(O,B.toolButtonAttributes,"showDateEditor");
return M
},todayControl:function(M){return(!M.calendar.options.disabled&&M.calendar.options.todayControlMode!="hidden"?B.getControl(M.controlLabels.today,B.toolButtonAttributes,"today"):"")
},closeControl:function(M){return(M.calendar.options.popup?B.getControl(M.controlLabels.close,B.toolButtonAttributes,"close","false"):"")
},applyControl:function(M){return(!M.calendar.options.disabled&&!M.calendar.options.readonly&&M.calendar.options.showApplyButton?B.getControl(M.controlLabels.apply,B.toolButtonAttributes,"close","true"):"")
},cleanControl:function(M){return(!M.calendar.options.disabled&&!M.calendar.options.readonly&&M.calendar.selectedDate?B.getControl(M.controlLabels.clean,B.toolButtonAttributes,"__resetSelectedDate"):"")
},selectedDateControl:function(M){return B.getSelectedDateControl(M.calendar)
},timeControl:function(M){return B.getTimeControl(M.calendar)
},timeEditorFields:function(M){return M.calendar.timePatternHtml
},header:[new E("table",{border:"0",cellpadding:"0",cellspacing:"0",width:"100%"},[new E("tbody",{},[new E("tr",{},[new E("td",{"class":"rf-cal-tl"},[new ET(function(M){return L.calendarTemplates.evalMacro("previousYearControl",M)
})]),new E("td",{"class":"rf-cal-tl"},[new ET(function(M){return L.calendarTemplates.evalMacro("previousMonthControl",M)
})]),new E("td",{"class":"rf-cal-hdr-month"},[new ET(function(M){return L.calendarTemplates.evalMacro("currentMonthControl",M)
})]),new E("td",{"class":"rf-cal-tl"},[new ET(function(M){return L.calendarTemplates.evalMacro("nextMonthControl",M)
})]),new E("td",{"class":"rf-cal-tl"},[new ET(function(M){return L.calendarTemplates.evalMacro("nextYearControl",M)
})]),new E("td",{"class":"rf-cal-tl rf-cal-btn-close",style:function(M){return(this.isEmpty?"display:none;":"")
}},[new ET(function(M){return L.calendarTemplates.evalMacro("closeControl",M)
})])])])])],footer:[new E("table",{border:"0",cellpadding:"0",cellspacing:"0",width:"100%"},[new E("tbody",{},[new E("tr",{},[new E("td",{"class":"rf-cal-tl-ftr",style:function(M){return(this.isEmpty?"display:none;":"")
}},[new ET(function(M){return L.calendarTemplates.evalMacro("selectedDateControl",M)
})]),new E("td",{"class":"rf-cal-tl-ftr",style:function(M){return(this.isEmpty?"display:none;":"")
}},[new ET(function(M){return L.calendarTemplates.evalMacro("cleanControl",M)
})]),new E("td",{"class":"rf-cal-tl-ftr",style:function(M){return(this.isEmpty?"display:none;":"")
}},[new ET(function(M){return L.calendarTemplates.evalMacro("timeControl",M)
})]),new E("td",{"class":"rf-cal-tl-ftr",style:"background-image:none;",width:"100%"},[]),new E("td",{"class":"rf-cal-tl-ftr",style:function(M){return(this.isEmpty?"display:none;":"")+(M.calendar.options.disabled||M.calendar.options.readonly||!M.calendar.options.showApplyButton?"background-image:none;":"")
}},[new ET(function(M){return L.calendarTemplates.evalMacro("todayControl",M)
})]),new E("td",{"class":"rf-cal-tl-ftr",style:function(M){return(this.isEmpty?"display:none;":"")+"background-image:none;"
}},[new ET(function(M){return L.calendarTemplates.evalMacro("applyControl",M)
})])])])])],timeEditorLayout:[new E("table",{id:function(M){return M.calendar.TIME_EDITOR_LAYOUT_ID
},border:"0",cellpadding:"0",cellspacing:"0","class":"rf-cal-timepicker-cnt"},[new E("tbody",{},[new E("tr",{},[new E("td",{"class":"rf-cal-timepicker-inp",colspan:"2",align:"center"},[new ET(function(M){return L.calendarTemplates.evalMacro("timeEditorFields",M)
})])]),new E("tr",{},[new E("td",{"class":"rf-cal-timepicker-ok"},[new E("div",{id:function(M){return M.calendar.TIME_EDITOR_BUTTON_OK
},"class":"rf-cal-time-btn",style:"float:right;",onmousedown:"RichFaces.jQuery(this).addClass('rf-cal-time-btn-press');",onmouseout:"RichFaces.jQuery(this).removeClass('rf-cal-time-btn-press');",onmouseup:"RichFaces.jQuery(this).removeClass('rf-cal-time-btn-press');",onclick:function(M){return"RichFaces.component('"+M.calendar.id+"').hideTimeEditor(true)"
}},[new E("span",{},[new ET(function(M){return M.controlLabels.ok
})])])]),new E("td",{"class":"rf-cal-timepicker-cancel"},[new E("div",{id:function(M){return M.calendar.TIME_EDITOR_BUTTON_CANCEL
},"class":"rf-cal-time-btn",style:"float:left;",onmousedown:"RichFaces.jQuery(this).addClass('rf-cal-time-btn-press');",onmouseout:"RichFaces.jQuery(this).removeClass('rf-cal-time-btn-press');",onmouseup:"RichFaces.jQuery(this).removeClass('rf-cal-time-btn-press');",onclick:function(M){return"RichFaces.component('"+M.calendar.id+"').hideTimeEditor(false)"
}},[new E("span",{},[new ET(function(M){return M.controlLabels.cancel
})])])])])])])],dayList:[new ET(function(M){return M.day
})],weekNumber:[new ET(function(M){return M.weekNumber
})],weekDay:[new ET(function(M){return M.weekDayLabelShort
})]};
var H=function(M){this.calendar=M;
this.monthLabels=M.options.monthLabels;
this.monthLabelsShort=M.options.monthLabelsShort;
this.weekDayLabels=M.options.weekDayLabels;
this.weekDayLabelsShort=M.options.weekDayLabelsShort;
this.controlLabels=M.options.labels
};
F.extend(H.prototype,{nextYearControl:B.nextYearControl,previousYearControl:B.previousYearControl,nextMonthControl:B.nextMonthControl,previousMonthControl:B.previousMonthControl,currentMonthControl:B.currentMonthControl,selectedDateControl:B.selectedDateControl,cleanControl:B.cleanControl,timeControl:B.timeControl,todayControl:B.todayControl,closeControl:B.closeControl,applyControl:B.applyControl,timeEditorFields:B.timeEditorFields});
var C={showWeekDaysBar:true,showWeeksBar:true,datePattern:"MMM d, yyyy",horizontalOffset:0,verticalOffset:0,dayListMarkup:B.dayList,weekNumberMarkup:B.weekNumber,weekDayMarkup:B.weekDay,headerMarkup:B.header,footerMarkup:B.footer,isDayEnabled:function(M){return true
},dayStyleClass:function(M){return""
},showHeader:true,showFooter:true,direction:"AA",jointPoint:"AA",popup:true,boundaryDatesMode:"inactive",todayControlMode:"select",style:"",className:"",disabled:false,readonly:false,enableManualInput:false,showInput:true,resetTimeOnDateSelect:false,style:"z-index: 3;",showApplyButton:false,selectedDate:null,currentDate:null,defaultTime:{hours:12,minutes:0,seconds:0},mode:"client",hidePopupOnScroll:true,defaultLabel:""};
var K={apply:"Apply",today:"Today",clean:"Clean",ok:"OK",cancel:"Cancel",close:"x"};
var I=["change","dateselect","beforedateselect","currentdateselect","beforecurrentdateselect","currentdateselect","clean","complete","collapse","datemouseout","datemouseover","show","hide","timeselect","beforetimeselect"];
var D=function(M){var N=L.getDomElement(this.INPUT_DATE_ID);
if((N.value==this.options.defaultLabel&&!M)||(M==this.options.defaultLabel&&!N.value)){N.value=M;
if(M){F(N).addClass("rf-cal-dflt-lbl")
}else{F(N).removeClass("rf-cal-dflt-lbl")
}}};
var G=function(M){this.isFocused=M.type=="focus";
if(!this.isFocused&&this.isVisible){return 
}D.call(this,(M.type=="focus"?"":this.options.defaultLabel))
};
L.ui.Calendar=function(j,e,S,q){J.constructor.call(this,j);
this.namespace="."+L.Event.createNamespace(this.name,j);
this.options=F.extend(this.options,C,A[e],S,q);
var c=S.labels||{};
for(var r in K){if(!c[r]){c[r]=K[r]
}}this.options.labels=c;
this.popupOffset=[this.options.horizontalOffset,this.options.verticalOffset];
if(!this.options.popup){this.options.showApplyButton=false
}this.options.boundaryDatesMode=this.options.boundaryDatesMode.toLowerCase();
this.hideBoundaryDatesContent=this.options.boundaryDatesMode=="hidden";
this.options.todayControlMode=this.options.todayControlMode.toLowerCase();
this.setTimeProperties();
this.customDayListMarkup=(this.options.dayListMarkup!=B.dayList);
this.currentDate=this.options.currentDate?this.options.currentDate:(this.options.selectedDate?this.options.selectedDate:new Date());
this.currentDate.setDate(1);
this.selectedDate=this.options.selectedDate;
this.todayDate=new Date();
this.firstWeekendDayNumber=6-this.options.firstWeekDay;
this.secondWeekendDayNumber=(this.options.firstWeekDay>0?7-this.options.firstWeekDay:0);
this.calendarContext=new H(this);
this.DATE_ELEMENT_ID=this.id+"DayCell";
this.WEEKNUMBER_BAR_ID=this.id+"WeekNum";
this.WEEKNUMBER_ELEMENT_ID=this.WEEKNUMBER_BAR_ID+"Cell";
this.WEEKDAY_BAR_ID=this.id+"WeekDay";
this.WEEKDAY_ELEMENT_ID=this.WEEKDAY_BAR_ID+"Cell";
this.POPUP_ID=this.id+"Popup";
this.POPUP_BUTTON_ID=this.id+"PopupButton";
this.INPUT_DATE_ID=this.id+"InputDate";
this.EDITOR_ID=this.id+"Editor";
this.EDITOR_SHADOW_ID=this.id+"EditorShadow";
this.TIME_EDITOR_LAYOUT_ID=this.id+"TimeEditorLayout";
this.DATE_EDITOR_LAYOUT_ID=this.id+"DateEditorLayout";
this.EDITOR_LAYOUT_SHADOW_ID=this.id+"EditorLayoutShadow";
this.TIME_EDITOR_BUTTON_OK=this.id+"TimeEditorButtonOk";
this.TIME_EDITOR_BUTTON_CANCEL=this.id+"TimeEditorButtonCancel";
this.DATE_EDITOR_BUTTON_OK=this.id+"DateEditorButtonOk";
this.DATE_EDITOR_BUTTON_CANCEL=this.id+"DateEditorButtonCancel";
this.CALENDAR_CONTENT=this.id+"Content";
this.firstDateIndex=0;
this.daysData={startDate:null,days:[]};
this.days=[];
this.todayCellId=null;
this.todayCellColor="";
this.selectedDateCellId=null;
this.selectedDateCellColor="";
var W="";
this.isVisible=true;
if(this.options.popup==true){W="display:none; position:absolute;";
this.isVisible=false
}var g="RichFaces.component('"+this.id+"').";
var X='<table id="'+this.CALENDAR_CONTENT+'" border="0" cellpadding="0" cellspacing="0" class="rf-cal-extr rf-cal-popup '+this.options.styleClass+'" style="'+W+this.options.style+'" onclick="'+g+'skipEventOnCollapse=true;"><tbody>';
var Q=(this.options.showWeeksBar?"8":"7");
var V=(this.options.optionalHeaderMarkup)?'<tr><td class="rf-cal-hdr-optnl" colspan="'+Q+'" id="'+this.id+'HeaderOptional"></td></tr>':"";
var h=(this.options.optionalFooterMarkup)?'<tr><td class="rf-cal-ftr-optl" colspan="'+Q+'" id="'+this.id+'FooterOptional"></td></tr>':"";
var m=(this.options.showHeader?'<tr><td class="rf-cal-hdr" colspan="'+Q+'" id="'+this.id+'Header"></td></tr>':"");
var d=(this.options.showFooter?'<tr><td class="rf-cal-ftr" colspan="'+Q+'" id="'+this.id+'Footer"></td></tr>':"");
var R="</tbody></table>";
var Z;
var M;
var Y=[];
var P;
var O=this.options.disabled||this.options.readonly?"":'onclick="'+g+'eventCellOnClick(event, this);" onmouseover="'+g+'eventCellOnMouseOver(event, this);" onmouseout="'+g+'eventCellOnMouseOut(event, this);"';
if(this.options.showWeekDaysBar){Y.push('<tr id="'+this.WEEKDAY_BAR_ID+'">');
if(this.options.showWeeksBar){Y.push('<td class="rf-cal-day-lbl"><br/></td>')
}var o=this.options.firstWeekDay;
for(var f=0;
f<7;
f++){P={weekDayLabel:this.options.weekDayLabels[o],weekDayLabelShort:this.options.weekDayLabelsShort[o],weekDayNumber:o,isWeekend:this.isWeekend(f),elementId:this.WEEKDAY_ELEMENT_ID+f,component:this};
var n=this.evaluateMarkup(this.options.weekDayMarkup,P);
if(o==6){o=0
}else{o++
}Z="rf-cal-day-lbl";
if(P.isWeekend){Z+=" rf-cal-holliday-lbl"
}if(f==6){Z+=" rf-cal-right-c"
}Y.push('<td class="'+Z+'" id="'+P.elementId+'">'+n+"</td>")
}Y.push("</tr>\n")
}var l=[];
var b=0;
this.dayCellClassName=[];
for(k=1;
k<7;
k++){M=(k==6?"rf-btm-c ":"");
l.push('<tr id="'+this.WEEKNUMBER_BAR_ID+k+'">');
if(this.options.showWeeksBar){P={weekNumber:k,elementId:this.WEEKNUMBER_ELEMENT_ID+k,component:this};
var U=this.evaluateMarkup(this.options.weekNumberMarkup,P);
l.push('<td class="rf-cal-week '+M+'" id="'+P.elementId+'">'+U+"</td>")
}for(var f=0;
f<7;
f++){Z=M+(!this.options.dayCellClass?"rf-cal-c-cnt-overflow":(!this.customDayListMarkup?this.options.dayCellClass:""))+" rf-cal-c";
if(f==this.firstWeekendDayNumber||f==this.secondWeekendDayNumber){Z+=" rf-cal-holiday"
}if(f==6){Z+=" rf-cal-right-c"
}this.dayCellClassName.push(Z);
l.push('<td class="'+Z+'" id="'+this.DATE_ELEMENT_ID+b+'" '+O+">"+(this.customDayListMarkup?'<div class="rf-cal-c-cnt'+(this.options.dayCellClass?" "+this.options.dayCellClass:"")+'"></div>':"")+"</td>");
b++
}l.push("</tr>")
}var a=L.getDomElement(this.CALENDAR_CONTENT);
a=F(a).replaceWith(X+V+m+Y.join("")+l.join("")+d+h+R);
this.attachToDom();
a=null;
if(this.options.popup&&!this.options.disabled){var N=new Function("event","RichFaces.component('"+this.id+"').switchPopup();");
L.Event.bindById(this.POPUP_BUTTON_ID,"click"+this.namespace,N,this);
if(!this.options.enableManualInput){L.Event.bindById(this.INPUT_DATE_ID,"click"+this.namespace,N,this)
}if(this.options.defaultLabel){D.call(this,this.options.defaultLabel);
L.Event.bindById(this.INPUT_DATE_ID,"focus"+this.namespace+" blur"+this.namespace,G,this)
}}this.scrollElements=null;
this.isAjaxMode=this.options.mode=="ajax"
};
L.BaseComponent.extend(L.ui.Calendar);
var J=L.ui.Calendar.$super;
var A={};
L.ui.Calendar.addLocale=function(M,N){if(!A[M]){A[M]=N
}};
F.extend(L.ui.Calendar.prototype,{name:"Calendar",destroy:function(){if(this.options.popup&&this.isVisible){this.scrollElements&&L.Event.unbindScrollEventHandlers(this.scrollElements,this);
this.scrollElements=null;
L.Event.unbind(window.document,"click"+this.namespace)
}J.destroy.call(this)
},dateEditorSelectYear:function(M){if(this.dateEditorYearID){F(L.getDomElement(this.dateEditorYearID)).removeClass("rf-cal-edtr-btn-sel")
}this.dateEditorYear=this.dateEditorStartYear+M;
this.dateEditorYearID=this.DATE_EDITOR_LAYOUT_ID+"Y"+M;
F(L.getDomElement(this.dateEditorYearID)).addClass("rf-cal-edtr-btn-sel")
},dateEditorSelectMonth:function(M){this.dateEditorMonth=M;
F(L.getDomElement(this.dateEditorMonthID)).removeClass("rf-cal-edtr-btn-sel");
this.dateEditorMonthID=this.DATE_EDITOR_LAYOUT_ID+"M"+M;
F(L.getDomElement(this.dateEditorMonthID)).addClass("rf-cal-edtr-btn-sel")
},scrollEditorYear:function(P){var N=L.getDomElement(this.DATE_EDITOR_LAYOUT_ID+"TR");
if(this.dateEditorYearID){F(L.getDomElement(this.dateEditorYearID)).removeClass("rf-cal-edtr-btn-sel");
this.dateEditorYearID=""
}if(!P){if(this.dateEditorMonth!=this.getCurrentMonth()){this.dateEditorMonth=this.getCurrentMonth();
F(L.getDomElement(this.dateEditorMonthID)).removeClass("rf-cal-edtr-btn-sel");
this.dateEditorMonthID=this.DATE_EDITOR_LAYOUT_ID+"M"+this.dateEditorMonth;
F(L.getDomElement(this.dateEditorMonthID)).addClass("rf-cal-edtr-btn-sel")
}}if(N){var Q;
var O=this.dateEditorStartYear=this.dateEditorStartYear+P*10;
for(var M=0;
M<5;
M++){N=N.nextSibling;
Q=N.firstChild.nextSibling.nextSibling;
Q.firstChild.innerHTML=O;
if(O==this.dateEditorYear){F(Q.firstChild).addClass("rf-cal-edtr-btn-sel");
this.dateEditorYearID=Q.firstChild.id
}Q=Q.nextSibling;
Q.firstChild.innerHTML=O+5;
if(O+5==this.dateEditorYear){F(Q.firstChild).addClass("rf-cal-edtr-btn-sel");
this.dateEditorYearID=Q.firstChild.id
}O++
}}},updateDateEditor:function(){this.dateEditorYear=this.getCurrentYear();
this.dateEditorStartYear=this.getCurrentYear()-4;
this.scrollEditorYear(0)
},updateTimeEditor:function(){var S=L.getDomElement(this.id+"TimeHours");
var R=L.getDomElement(this.id+"TimeSign");
var O=L.getDomElement(this.id+"TimeMinutes");
var Q=this.selectedDate.getHours();
var M=this.selectedDate.getMinutes();
if(this.timeType==2){var N=(Q<12?"AM":"PM");
R.value=N;
Q=(Q==0?"12":(Q>12?Q-12:Q))
}S.value=(this.timeHoursDigits==2&&Q<10?"0"+Q:Q);
O.value=(M<10?"0"+M:M);
if(this.showSeconds){var U=L.getDomElement(this.id+"TimeSeconds");
var P=this.selectedDate.getSeconds();
U.value=(P<10?"0"+P:P)
}},createEditor:function(){var P=F(L.getDomElement(this.CALENDAR_CONTENT));
var O=parseInt(P.css("z-index"),10);
var M='<div id="'+this.EDITOR_SHADOW_ID+'" class="rf-cal-edtr-shdw" style="position:absolute; display:none;z-index:'+O+'"></div><table border="0" cellpadding="0" cellspacing="0" id="'+this.EDITOR_ID+'" style="position:absolute; display:none;z-index:'+(O+1)+'" onclick="RichFaces.component(\''+this.id+'\').skipEventOnCollapse=true;"><tbody><tr><td class="rf-cal-edtr-cntr" align="center"><div style="position:relative; display:inline-block;">';
var Q='<div id="'+this.EDITOR_LAYOUT_SHADOW_ID+'" class="rf-cal-edtr-layout-shdw"></div>';
var N="</div></td></tr></tbody></table>";
P.after(M+Q+N);
this.isEditorCreated=true;
return L.getDomElement(this.EDITOR_ID)
},createTimeEditorLayout:function(N){F(L.getDomElement(this.EDITOR_LAYOUT_SHADOW_ID)).after(this.evaluateMarkup(B.timeEditorLayout,this.calendarContext));
var P=L.getDomElement(this.id+"TimeHours");
var O;
var M=L.getDomElement(this.id+"TimeMinutes");
if(this.timeType==1){sbjQuery(P).SpinButton({digits:this.timeHoursDigits,min:0,max:23})
}else{sbjQuery(P).SpinButton({digits:this.timeHoursDigits,min:1,max:12});
O=L.getDomElement(this.id+"TimeSign");
sbjQuery(O).SpinButton({})
}sbjQuery(M).SpinButton({digits:2,min:0,max:59});
if(this.showSeconds){var Q=L.getDomElement(this.id+"TimeSeconds");
sbjQuery(Q).SpinButton({digits:2,min:0,max:59})
}this.correctEditorButtons(N,this.TIME_EDITOR_BUTTON_OK,this.TIME_EDITOR_BUTTON_CANCEL);
this.isTimeEditorLayoutCreated=true
},correctEditorButtons:function(Q,N,M){var S=L.getDomElement(N);
var P=L.getDomElement(M);
Q.style.visibility="hidden";
Q.style.display="";
var R=F(S.firstChild).width();
var O=F(P.firstChild).width();
Q.style.display="none";
Q.style.visibility="";
if(R!=O){S.style.width=P.style.width=(R>O?R:O)+"px"
}},createDECell:function(R,P,M,Q,O){if(M==0){return'<div id="'+R+'" class="rf-cal-edtr-btn'+(O?" "+O:"")+'" onmouseover="this.className=\'rf-cal-edtr-btn rf-cal-edtr-tl-over\';" onmouseout="this.className=\'rf-cal-edtr-btn\';" onmousedown="this.className=\'rf-cal-edtr-btn rf-cal-edtr-tl-press\';" onmouseup="this.className=\'rf-cal-edtr-btn rf-cal-edtr-tl-over\';" onclick="RichFaces.component(\''+this.id+"').scrollEditorYear("+Q+');">'+P+"</div>"
}else{var N=(M==1?"RichFaces.component('"+this.id+"').dateEditorSelectMonth("+Q+");":"RichFaces.component('"+this.id+"').dateEditorSelectYear("+Q+");");
return'<div id="'+R+'" class="rf-cal-edtr-btn'+(O?" "+O:"")+'" onmouseover="RichFaces.jQuery(this).addClass(\'rf-cal-edtr-btn-over\');" onmouseout="$(this).removeClass(\'rf-cal-edtr-btn-over\');" onclick="'+N+'">'+P+"</div>"
}},createDateEditorLayout:function(Q){var M='<table id="'+this.DATE_EDITOR_LAYOUT_ID+'" class="rf-cal-monthpicker-cnt" border="0" cellpadding="0" cellspacing="0"><tbody><tr id="'+this.DATE_EDITOR_LAYOUT_ID+'TR">';
var N="</tr></tbody></table>";
var R=0;
this.dateEditorYear=this.getCurrentYear();
var P=this.dateEditorStartYear=this.dateEditorYear-4;
var S='<td align="center">'+this.createDECell(this.DATE_EDITOR_LAYOUT_ID+"M"+R,this.options.monthLabelsShort[R],1,R)+'</td><td align="center" class="rf-cal-monthpicker-split">'+this.createDECell(this.DATE_EDITOR_LAYOUT_ID+"M"+(R+6),this.options.monthLabelsShort[R+6],1,R+6)+'</td><td align="center">'+this.createDECell("","&lt;",0,-1)+'</td><td align="center">'+this.createDECell("","&gt;",0,1)+"</td>";
R++;
for(var O=0;
O<5;
O++){S+='</tr><tr><td align="center">'+this.createDECell(this.DATE_EDITOR_LAYOUT_ID+"M"+R,this.options.monthLabelsShort[R],1,R)+'</td><td align="center" class="rf-cal-monthpicker-split">'+this.createDECell(this.DATE_EDITOR_LAYOUT_ID+"M"+(R+6),this.options.monthLabelsShort[R+6],1,R+6)+'</td><td align="center">'+this.createDECell(this.DATE_EDITOR_LAYOUT_ID+"Y"+O,P,2,O,(O==4?"rf-cal-edtr-btn-sel":""))+'</td><td align="center">'+this.createDECell(this.DATE_EDITOR_LAYOUT_ID+"Y"+(O+5),P+5,2,O+5)+"</td>";
R++;
P++
}this.dateEditorYearID=this.DATE_EDITOR_LAYOUT_ID+"Y4";
this.dateEditorMonth=this.getCurrentMonth();
this.dateEditorMonthID=this.DATE_EDITOR_LAYOUT_ID+"M"+this.dateEditorMonth;
S+='</tr><tr><td colspan="2" class="rf-cal-monthpicker-ok"><div id="'+this.DATE_EDITOR_BUTTON_OK+'" class="rf-cal-time-btn" style="float:right;" onmousedown="RichFaces.jQuery(this).addClass(\'rf-cal-time-btn-press\');" onmouseout="RichFaces.jQuery(this).removeClass(\'rf-cal-time-btn-press\');" onmouseup="RichFaces.jQuery(this).removeClass(\'rf-cal-time-btn-press\');" onclick="RichFaces.component(\''+this.id+"').hideDateEditor(true);\"><span>"+this.options.labels.ok+'</span></div></td><td colspan="2" class="rf-cal-monthpicker-cancel"><div id="'+this.DATE_EDITOR_BUTTON_CANCEL+'" class="rf-cal-time-btn" style="float:left;" onmousedown="RichFaces.jQuery(this).addClass(\'rf-cal-time-btn-press\');" onmouseout="RichFaces.jQuery(this).removeClass(\'rf-cal-time-btn-press\');" onmouseup="RichFaces.jQuery(this).removeClass(\'rf-cal-time-btn-press\');" onclick="RichFaces.component(\''+this.id+"').hideDateEditor(false);\"><span>"+this.options.labels.cancel+"</span></div></td>";
F(L.getDomElement(this.EDITOR_LAYOUT_SHADOW_ID)).after(M+S+N);
F(L.getDomElement(this.dateEditorMonthID)).addClass("rf-cal-edtr-btn-sel");
this.correctEditorButtons(Q,this.DATE_EDITOR_BUTTON_OK,this.DATE_EDITOR_BUTTON_CANCEL);
this.isDateEditorLayoutCreated=true
},createSpinnerTable:function(M){return'<table cellspacing="0" cellpadding="0" border="0"><tbody><tr><td class="rf-cal-sp-inp-ctnr"><input id="'+M+'" name="'+M+'" class="rf-cal-sp-inp" type="text" /></td><td class="rf-cal-sp-btn"><table border="0" cellspacing="0" cellpadding="0"><tbody><tr><td><div id="'+M+'BtnUp" class="rf-cal-sp-up" onmousedown="this.className=\'rf-cal-sp-up rf-cal-sp-press\'" onmouseup="this.className=\'rf-cal-sp-up\'" onmouseout="this.className=\'rf-cal-sp-up\'"><span></span></div></td></tr><tr><td><div id="'+M+'BtnDown" class="rf-cal-sp-down" onmousedown="this.className=\'rf-cal-sp-down rf-cal-sp-press\'" onmouseup="this.className=\'rf-cal-sp-down\'" onmouseout="this.className=\'rf-cal-sp-down\'"><span></span></div></td></tr></tbody></table></td></tr></tbody></table>'
},setTimeProperties:function(){this.timeType=0;
var Q=this.options.datePattern;
var c=[];
var X=/(\\\\|\\[yMdaHhms])|(y+|M+|d+|a|H{1,2}|h{1,2}|m{2}|s{2})/g;
var V;
while(V=X.exec(Q)){if(!V[1]){c.push({str:V[0],marker:V[2],idx:V.index})
}}var M="";
var d="";
var O,b,N,Y,U,e;
var W=this.id;
var f=function(a){return(a.length==0?R.marker:Q.substring(c[Z-1].str.length+c[Z-1].idx,R.idx+R.str.length))
};
for(var Z=0;
Z<c.length;
Z++){var R=c[Z];
var P=R.marker.charAt(0);
if(P=="y"||P=="M"||P=="d"){M+=f(M)
}else{if(P=="a"){e=true;
d+=f(d)
}else{if(P=="H"){b=true;
O=R.marker.length;
d+=f(d)
}else{if(P=="h"){N=true;
O=R.marker.length;
d+=f(d)
}else{if(P=="m"){Y=true;
d+=f(d)
}else{if(P=="s"){this.showSeconds=true;
d+=f(d)
}}}}}}}this.datePattern=M;
this.timePattern=d;
var S=this;
this.timePatternHtml=d.replace(/(\\\\|\\[yMdaHhms])|(H{1,2}|h{1,2}|m{2}|s{2}|a)/g,function(a,h,g){if(h){return h.charAt(1)
}switch(g){case"a":return"</td><td>"+S.createSpinnerTable(W+"TimeSign")+"</td><td>";
case"H":case"HH":case"h":case"hh":return"</td><td>"+S.createSpinnerTable(W+"TimeHours")+"</td><td>";
case"mm":return"</td><td>"+S.createSpinnerTable(W+"TimeMinutes")+"</td><td>";
case"ss":return"</td><td>"+S.createSpinnerTable(W+"TimeSeconds")+"</td><td>"
}});
this.timePatternHtml='<table border="0" cellpadding="0"><tbody><tr><td>'+this.timePatternHtml+"</td></tr></tbody></table>";
if(Y&&b){this.timeType=1
}else{if(Y&&N&&e){this.timeType=2
}}this.timeHoursDigits=O
},eventOnScroll:function(M){this.hidePopup()
},hidePopup:function(){if(!this.options.popup||!this.isVisible){return 
}if(this.invokeEvent("hide",L.getDomElement(this.id))){if(this.isEditorVisible){this.hideEditor()
}this.scrollElements&&L.Event.unbindScrollEventHandlers(this.scrollElements,this);
this.scrollElements=null;
L.Event.unbind(window.document,"click"+this.namespace);
F(L.getDomElement(this.CALENDAR_CONTENT)).hide();
this.isVisible=false;
if(this.options.defaultLabel&&!this.isFocused){D.call(this,this.options.defaultLabel)
}}},showPopup:function(P){if(!this.isRendered){this.isRendered=true;
this.render()
}this.skipEventOnCollapse=false;
if(P&&P.type=="click"){this.skipEventOnCollapse=true
}if(!this.options.popup||this.isVisible){return 
}var M=L.getDomElement(this.id);
if(this.invokeEvent("show",M,P)){var O=L.getDomElement(this.POPUP_ID);
var Q=O.firstChild;
var N=Q.nextSibling;
if(this.options.defaultLabel){if(!this.isFocused){D.call(this,"")
}}if(Q.value){this.__selectDate(Q.value,false,{event:P,element:M})
}if(this.options.showInput){O=O.children
}else{O=N
}F(L.getDomElement(this.CALENDAR_CONTENT)).setPosition(O,{type:"DROPDOWN",from:this.options.jointPoint,to:this.options.direction,offset:this.popupOffset}).show();
this.isVisible=true;
L.Event.bind(window.document,"click"+this.namespace,this.eventOnCollapse,this);
this.scrollElements&&L.Event.unbindScrollEventHandlers(this.scrollElements,this);
this.scrollElements=null;
if(this.options.hidePopupOnScroll){this.scrollElements=L.Event.bindScrollEventHandlers(M,this.eventOnScroll,this)
}}},switchPopup:function(M){this.isVisible?this.hidePopup():this.showPopup(M)
},eventOnCollapse:function(M){if(this.skipEventOnCollapse){this.skipEventOnCollapse=false;
return true
}if(M.target.id==this.POPUP_BUTTON_ID||(!this.options.enableManualInput&&M.target.id==this.INPUT_DATE_ID)){return true
}this.hidePopup();
return true
},setInputField:function(M,N){var O=L.getDomElement(this.INPUT_DATE_ID);
if(O.value!=M){O.value=M;
this.invokeEvent("change",L.getDomElement(this.id),N,this.selectedDate);
F(L.getDomElement(this.INPUT_DATE_ID)).blur()
}},getCurrentDate:function(){return this.currentDate
},__getSelectedDate:function(){if(!this.selectedDate){return null
}else{return this.selectedDate
}},__getSelectedDateString:function(M){if(!this.selectedDate){return""
}if(!M){M=this.options.datePattern
}return L.calendarUtils.formatDate(this.selectedDate,M,this.options.monthLabels,this.options.monthLabelsShort)
},getPrevYear:function(){var M=this.currentDate.getFullYear()-1;
if(M<0){M=0
}return M
},getPrevMonth:function(M){var N=this.currentDate.getMonth()-1;
if(N<0){N=11
}if(M){return this.options.monthLabels[N]
}else{return N
}},getCurrentYear:function(){return this.currentDate.getFullYear()
},getCurrentMonth:function(M){var N=this.currentDate.getMonth();
if(M){return this.options.monthLabels[N]
}else{return N
}},getNextYear:function(){return this.currentDate.getFullYear()+1
},getNextMonth:function(M){var N=this.currentDate.getMonth()+1;
if(N>11){N=0
}if(M){return this.options.monthLabels[N]
}else{return N
}},isWeekend:function(M){return(M==this.firstWeekendDayNumber||M==this.secondWeekendDayNumber)
},setupTimeForDate:function(N){var M=new Date(N);
if(this.selectedDate&&(!this.options.resetTimeOnDateSelect||(this.selectedDate.getFullYear()==N.getFullYear()&&this.selectedDate.getMonth()==N.getMonth()&&this.selectedDate.getDate()==N.getDate()))){M=L.calendarUtils.createDate(N.getFullYear(),N.getMonth(),N.getDate(),this.selectedDate.getHours(),this.selectedDate.getMinutes(),this.selectedDate.getSeconds())
}else{M=L.calendarUtils.createDate(N.getFullYear(),N.getMonth(),N.getDate(),this.options.defaultTime.hours,this.options.defaultTime.minutes,this.options.defaultTime.seconds)
}return M
},eventCellOnClick:function(P,O){var N=this.days[parseInt(O.id.substr(this.DATE_ELEMENT_ID.length),10)];
if(N.enabled&&N._month==0){var M=L.calendarUtils.createDate(this.currentDate.getFullYear(),this.currentDate.getMonth(),N.day);
if(this.timeType){M=this.setupTimeForDate(M)
}if(this.__selectDate(M,true,{event:P,element:O})&&!this.options.showApplyButton){this.hidePopup()
}}else{if(N._month!=0){if(this.options.boundaryDatesMode=="scroll"){if(N._month==-1){this.prevMonth()
}else{this.nextMonth()
}}else{if(this.options.boundaryDatesMode=="select"){var M=new Date(N.date);
if(this.timeType){M=this.setupTimeForDate(M)
}if(this.__selectDate(M,false,{event:P,element:O})&&!this.options.showApplyButton){this.hidePopup()
}}}}}},eventCellOnMouseOver:function(O,N){var M=this.days[parseInt(N.id.substr(this.DATE_ELEMENT_ID.length),10)];
if(this.invokeEvent("datemouseover",N,O,M.date)&&M.enabled){if(M._month==0&&N.id!=this.selectedDateCellId&&N.id!=this.todayCellId){F(N).addClass("rf-cal-hov")
}}},eventCellOnMouseOut:function(O,N){var M=this.days[parseInt(N.id.substr(this.DATE_ELEMENT_ID.length),10)];
if(this.invokeEvent("datemouseout",N,O,M.date)&&M.enabled){if(M._month==0&&N.id!=this.selectedDateCellId&&N.id!=this.todayCellId){F(N).removeClass("rf-cal-hov")
}}},load:function(N,M){if(N){this.daysData=this.indexData(N,M)
}else{this.daysData=null
}this.isRendered=false;
if(this.isVisible){this.render()
}if(typeof this.afterLoad=="function"){this.afterLoad();
this.afterLoad=null
}},indexData:function(Q,N){var O=Q.startDate.year;
var P=Q.startDate.month;
Q.startDate=new Date(O,P);
Q.index=[];
Q.index[O+"-"+P]=0;
if(N){this.currentDate=Q.startDate;
this.currentDate.setDate(1);
return Q
}var M=L.calendarUtils.daysInMonthByDate(Q.startDate)-Q.startDate.getDate()+1;
while(Q.days[M]){if(P==11){O++;
P=0
}else{P++
}Q.index[O+"-"+P]=M;
M+=(32-new Date(O,P,32).getDate())
}return Q
},getCellBackgroundColor:function(M){return F(M).css("background-color")
},clearEffect:function(M,N,P){if(M){var O=F(L.getDomElement(M)).stop(true,true);
if(N){O.removeClass(N)
}if(P){O.addClass(P)
}}return null
},render:function(){this.isRendered=true;
this.todayDate=new Date();
var r=this.getCurrentYear();
var d=this.getCurrentMonth();
var X=(r==this.todayDate.getFullYear()&&d==this.todayDate.getMonth());
var c=this.todayDate.getDate();
var f=this.selectedDate&&(r==this.selectedDate.getFullYear()&&d==this.selectedDate.getMonth());
var j=this.selectedDate&&this.selectedDate.getDate();
var S=L.calendarUtils.getDay(this.currentDate,this.options.firstWeekDay);
var R=L.calendarUtils.daysInMonthByDate(this.currentDate);
var M=L.calendarUtils.daysInMonth(r,d-1);
var b=0;
var q=-1;
this.days=[];
var W=M-S+1;
if(S>0){while(W<=M){this.days.push({day:W,isWeekend:this.isWeekend(b),_month:q});
W++;
b++
}}W=1;
q=0;
this.firstDateIndex=b;
if(this.daysData&&this.daysData.index[r+"-"+d]!=undefined){var a=this.daysData.index[r+"-"+d];
if(this.daysData.startDate.getFullYear()==r&&this.daysData.startDate.getMonth()==d){var V=V=(this.daysData.days[a].day?this.daysData.days[a].day:this.daysData.startDate.getDate());
while(W<V){this.days.push({day:W,isWeekend:this.isWeekend(b%7),_month:q});
W++;
b++
}}var i=this.daysData.days.length;
var Y;
var h;
while(a<i&&W<=R){h=this.isWeekend(b%7);
Y=this.daysData.days[a];
Y.day=W;
Y.isWeekend=h;
Y._month=q;
this.days.push(Y);
a++;
W++;
b++
}}while(b<42){if(W>R){W=1;
q=1
}this.days.push({day:W,isWeekend:this.isWeekend(b%7),_month:q});
W++;
b++
}this.renderHF();
b=0;
var N;
var U;
var P;
if(this.options.showWeeksBar){P=L.calendarUtils.weekNumber(r,d,this.options.minDaysInFirstWeek,this.options.firstWeekDay)
}this.selectedDayElement=null;
var Z=true;
var l;
var n=(this.options.boundaryDatesMode=="scroll"||this.options.boundaryDatesMode=="select");
this.todayCellId=this.clearEffect(this.todayCellId);
this.selectedDateCellId=this.clearEffect(this.selectedDateCellId);
var Y=L.getDomElement(this.WEEKNUMBER_BAR_ID+"1");
for(var g=1;
g<7;
g++){U=this.days[b];
N=Y.firstChild;
var m;
if(this.options.showWeeksBar){if(Z&&d==11&&(g==5||g==6)&&(U._month==1||(7-(R-U.day+1))>=this.options.minDaysInFirstWeek)){P=1;
Z=false
}m=P;
N.innerHTML=this.evaluateMarkup(this.options.weekNumberMarkup,{weekNumber:P++,elementId:N.id,component:this});
if(g==1&&P>52){P=1
}N=N.nextSibling
}var s=this.options.firstWeekDay;
var Q=null;
while(N){U.elementId=N.id;
U.date=new Date(r,d+U._month,U.day);
U.weekNumber=m;
U.component=this;
U.isCurrentMonth=(U._month==0);
U.weekDayNumber=s;
if(U.enabled!=false){U.enabled=this.options.isDayEnabled(U)
}if(!U.styleClass){U.customStyleClass=this.options.dayStyleClass(U)
}else{var O=this.options.dayStyleClass(U);
U.customStyleClass=U.styleClass;
if(O){U.customStyleClass+=" "+O
}}Q=(this.customDayListMarkup?N.firstChild:N);
Q.innerHTML=this.hideBoundaryDatesContent&&U._month!=0?"":this.evaluateMarkup(this.options.dayListMarkup,U);
if(s==6){s=0
}else{s++
}var o=this.dayCellClassName[b];
if(U._month!=0){o+=" rf-cal-boundary-day";
if(!this.options.disabled&&!this.options.readonly&&n){o+=" rf-cal-btn"
}}else{if(X&&U.day==c){this.todayCellId=N.id;
this.todayCellColor=this.getCellBackgroundColor(N);
o+=" rf-cal-today"
}if(f&&U.day==j){this.selectedDateCellId=N.id;
this.selectedDateCellColor=this.getCellBackgroundColor(N);
o+=" rf-cal-sel"
}else{if(!this.options.disabled&&!this.options.readonly&&U.enabled){o+=" rf-cal-btn"
}}if(U.customStyleClass){o+=" "+U.customStyleClass
}}N.className=o;
b++;
U=this.days[b];
N=N.nextSibling
}Y=Y.nextSibling
}},renderHF:function(){if(this.options.showHeader){this.renderMarkup(this.options.headerMarkup,this.id+"Header",this.calendarContext)
}if(this.options.showFooter){this.renderMarkup(this.options.footerMarkup,this.id+"Footer",this.calendarContext)
}this.renderHeaderOptional();
this.renderFooterOptional()
},renderHeaderOptional:function(){this.renderMarkup(this.options.optionalHeaderMarkup,this.id+"HeaderOptional",this.calendarContext)
},renderFooterOptional:function(){this.renderMarkup(this.options.optionalFooterMarkup,this.id+"FooterOptional",this.calendarContext)
},renderMarkup:function(N,M,O){if(!N){return 
}var P=L.getDomElement(M);
if(!P){return 
}P.innerHTML=this.evaluateMarkup(N,O)
},evaluateMarkup:function(O,Q){if(!O){return""
}var N=[];
var M;
for(var P=0;
P<O.length;
P++){M=O[P];
if(M.getContent){N.push(M.getContent(Q))
}}return N.join("")
},onUpdate:function(){var M=L.calendarUtils.formatDate(this.getCurrentDate(),"MM/yyyy");
L.getDomElement(this.id+"InputCurrentDate").value=M;
if(this.isAjaxMode&&this.callAjax){this.callAjax.call(this,M)
}else{this.render()
}},callAjax:function(P,M){var R=this;
var O=function(S){var U=S&&S.componentData&&S.componentData[R.id];
R.load(U,true)
};
var N=function(S){};
var Q={};
Q[this.id+".ajax"]="1";
L.ajax(this.id,null,{parameters:Q,error:N,complete:O})
},nextMonth:function(){this.changeCurrentDateOffset(0,1)
},prevMonth:function(){this.changeCurrentDateOffset(0,-1)
},nextYear:function(){this.changeCurrentDateOffset(1,0)
},prevYear:function(){this.changeCurrentDateOffset(-1,0)
},changeCurrentDate:function(N,P,O){if(this.getCurrentMonth()!=P||this.getCurrentYear()!=N){var M=new Date(N,P,1);
if(this.invokeEvent("beforecurrentdateselect",L.getDomElement(this.id),null,M)){this.currentDate=M;
if(O){this.render()
}else{this.onUpdate()
}this.invokeEvent("currentdateselect",L.getDomElement(this.id),null,M);
return true
}}return false
},changeCurrentDateOffset:function(N,O){var M=new Date(this.currentDate.getFullYear()+N,this.currentDate.getMonth()+O,1);
if(this.invokeEvent("beforecurrentdateselect",L.getDomElement(this.id),null,M)){this.currentDate=M;
this.onUpdate();
this.invokeEvent("currentdateselect",L.getDomElement(this.id),null,M)
}},today:function(P,R){var N=new Date();
var Q=N.getFullYear();
var S=N.getMonth();
var O=N.getDate();
var M=false;
if(O!=this.todayDate.getDate()){M=true;
this.todayDate=N
}if(Q!=this.currentDate.getFullYear()||S!=this.currentDate.getMonth()){M=true;
this.currentDate=new Date(Q,S,1)
}if(this.options.todayControlMode=="select"){R=true
}if(M){if(P){this.render()
}else{this.onUpdate()
}}else{if(this.isVisible&&this.todayCellId&&!R){this.clearEffect(this.todayCellId);
if(this.todayCellColor!="transparent"){F(L.getDomElement(this.todayCellId)).effect("highlight",{easing:"easeInOutSine",color:this.todayCellColor},300)
}}}if(this.options.todayControlMode=="select"&&!this.options.disabled&&!this.options.readonly){if(M&&!P&&this.submitFunction){this.afterLoad=this.selectToday
}else{this.selectToday()
}}},selectToday:function(){if(this.todayCellId){var O=this.days[parseInt(this.todayCellId.substr(this.DATE_ELEMENT_ID.length),10)];
var M=new Date();
var N=new Date(M);
if(this.timeType){N=this.setupTimeForDate(N)
}if(O.enabled&&this.__selectDate(N,true)&&!this.options.showApplyButton){this.hidePopup()
}}},__selectDate:function(P,N,V,O){if(!V){V={event:null,element:null}
}if(typeof O==="undefined"){O=!this.options.showApplyButton
}var M=this.selectedDate;
var W;
if(P){if(typeof P=="string"){P=L.calendarUtils.parseDate(P,this.options.datePattern,this.options.monthLabels,this.options.monthLabelsShort)
}W=P
}else{W=null
}var R=true;
var S=false;
if((M-W)&&(M!=null||W!=null)){S=true;
R=this.invokeEvent("beforedateselect",V.element,V.event,P)
}if(R){if(W!=null){if(W.getMonth()==this.currentDate.getMonth()&&W.getFullYear()==this.currentDate.getFullYear()){this.selectedDate=W;
if(!M||(M-this.selectedDate)){var Q=F(L.getDomElement(this.DATE_ELEMENT_ID+(this.firstDateIndex+this.selectedDate.getDate()-1)));
this.clearEffect(this.selectedDateCellId,"rf-cal-sel",(this.options.disabled||this.options.readonly?null:"rf-cal-btn"));
this.selectedDateCellId=Q.attr("id");
this.selectedDateCellColor=this.getCellBackgroundColor(Q);
Q.removeClass("rf-cal-btn");
Q.removeClass("rf-cal-hov");
Q.addClass("rf-cal-sel");
this.renderHF()
}else{if(this.timeType!=0){this.renderHF()
}}}else{this.selectedDate=W;
if(this.changeCurrentDate(W.getFullYear(),W.getMonth(),N)){}else{this.selectedDate=M;
S=false
}}}else{this.selectedDate=null;
this.clearEffect(this.selectedDateCellId,"rf-cal-sel",(this.options.disabled||this.options.readonly?null:"rf-cal-btn"));
if(this.selectedDateCellId){this.selectedDateCellId=null;
this.renderHF()
}var P=new Date();
if(this.currentDate.getMonth()==P.getMonth()&&this.currentDate.getFullYear()==P.getFullYear()){this.renderHF()
}var U=this.options.todayControlMode;
this.options.todayControlMode="";
this.today(N,true);
this.options.todayControlMode=U
}if(S){this.invokeEvent("dateselect",V.element,V.event,this.selectedDate);
if(O===true){this.setInputField(this.selectedDate!=null?this.__getSelectedDateString(this.options.datePattern):"",V.event)
}}}return S
},__resetSelectedDate:function(){if(!this.selectedDate){return 
}if(this.invokeEvent("beforedateselect",null,null,null)){this.selectedDate=null;
this.invokeEvent("dateselect",null,null,null);
this.selectedDateCellId=this.clearEffect(this.selectedDateCellId,"rf-cal-sel",(this.options.disabled||this.options.readonly?null:"rf-cal-btn"));
this.invokeEvent("clean",null,null,null);
this.renderHF();
if(!this.options.showApplyButton){this.setInputField("",null);
this.hidePopup()
}}},showSelectedDate:function(){if(!this.selectedDate){return 
}if(this.currentDate.getMonth()!=this.selectedDate.getMonth()||this.currentDate.getFullYear()!=this.selectedDate.getFullYear()){this.currentDate=new Date(this.selectedDate);
this.currentDate.setDate(1);
this.onUpdate()
}else{if(this.isVisible&&this.selectedDateCellId){this.clearEffect(this.selectedDateCellId);
if(this.selectedDateCellColor!="transparent"){F(L.getDomElement(this.selectedDateCellId)).effect("highlight",{easing:"easeInOutSine",color:this.selectedDateCellColor},300)
}}}},close:function(M){if(M){this.setInputField(this.__getSelectedDateString(this.options.datePattern),null)
}this.hidePopup()
},clonePosition:function(M,N,R){var Q=F(M);
if(!N.length){N=[N]
}R=R||{left:0,top:0};
var O=Q.outerWidth()+"px",X=Q.outerHeight()+"px";
var W=Q.position();
var P=Math.floor(W.left)+R.left+"px",V=Math.floor(W.top)+R.top+"px";
var U;
for(var S=0;
S<N.length;
S++){U=N[S];
U.style.width=O;
U.style.height=X;
U.style.left=P;
U.style.top=V
}},showTimeEditor:function(){var N;
if(this.timeType==0){return 
}if(!this.isEditorCreated){N=this.createEditor()
}else{N=L.getDomElement(this.EDITOR_ID)
}if(!this.isTimeEditorLayoutCreated){this.createTimeEditorLayout(N)
}F(L.getDomElement(this.TIME_EDITOR_LAYOUT_ID)).show();
var M=L.getDomElement(this.EDITOR_SHADOW_ID);
this.clonePosition(L.getDomElement(this.CALENDAR_CONTENT),[N,M]);
this.updateTimeEditor();
F(M).show();
F(N).show();
this.clonePosition(L.getDomElement(this.TIME_EDITOR_LAYOUT_ID),L.getDomElement(this.EDITOR_LAYOUT_SHADOW_ID),{left:3,top:3});
this.isEditorVisible=true
},hideEditor:function(){if(this.isTimeEditorLayoutCreated){F(L.getDomElement(this.TIME_EDITOR_LAYOUT_ID)).hide()
}if(this.isDateEditorLayoutCreated){F(L.getDomElement(this.DATE_EDITOR_LAYOUT_ID)).hide()
}F(L.getDomElement(this.EDITOR_ID)).hide();
F(L.getDomElement(this.EDITOR_SHADOW_ID)).hide();
this.isEditorVisible=false
},hideTimeEditor:function(O){this.hideEditor();
if(O&&this.selectedDate){var Q=this.showSeconds?parseInt(L.getDomElement(this.id+"TimeSeconds").value,10):this.options.defaultTime.seconds;
var M=parseInt(L.getDomElement(this.id+"TimeMinutes").value,10);
var P=parseInt(L.getDomElement(this.id+"TimeHours").value,10);
if(this.timeType==2){if(L.getDomElement(this.id+"TimeSign").value.toLowerCase()=="am"){if(P==12){P=0
}}else{if(P!=12){P+=12
}}}var N=L.calendarUtils.createDate(this.selectedDate.getFullYear(),this.selectedDate.getMonth(),this.selectedDate.getDate(),P,M,Q);
if(N-this.selectedDate&&this.invokeEvent("beforetimeselect",null,null,N)){this.selectedDate=N;
this.renderHF();
if(!this.options.popup||!this.options.showApplyButton){this.setInputField(this.__getSelectedDateString(this.options.datePattern),null)
}this.invokeEvent("timeselect",null,null,this.selectedDate)
}}if(this.options.popup&&!this.options.showApplyButton){this.close(false)
}},showDateEditor:function(){var N;
if(!this.isEditorCreated){N=this.createEditor()
}else{N=L.getDomElement(this.EDITOR_ID)
}if(!this.isDateEditorLayoutCreated){this.createDateEditorLayout(N)
}else{this.updateDateEditor()
}F(L.getDomElement(this.DATE_EDITOR_LAYOUT_ID)).show();
var M=L.getDomElement(this.EDITOR_SHADOW_ID);
this.clonePosition(L.getDomElement(this.CALENDAR_CONTENT),[N,M]);
F(M).show();
F(N).show();
this.clonePosition(L.getDomElement(this.DATE_EDITOR_LAYOUT_ID),L.getDomElement(this.EDITOR_LAYOUT_SHADOW_ID),{left:3,top:3});
this.isEditorVisible=true
},hideDateEditor:function(M){this.hideEditor();
if(M){this.changeCurrentDate(this.dateEditorYear,this.dateEditorMonth)
}},getValue:function(){return this.__getSelectedDate()
},getValueAsString:function(M){return this.__getSelectedDateString(M)
},setValue:function(M){this.__selectDate(M,undefined,undefined,true)
},resetValue:function(){this.__resetSelectedDate();
if(this.options.defaultLabel&&!this.isFocused){D.call(this,this.options.defaultLabel)
}},getNamespace:function(){return this.namespace
}})
})(RichFaces.jQuery,RichFaces);;(function(B,A){A.ui=A.ui||{};
A.ui.MenuKeyNavigation={__updateItemsList:function(){var C=B("."+this.options.cssClasses.listContainerCss+":first",this.popup.popup).find(">."+this.options.cssClasses.itemCss).not("."+this.options.cssClasses.disabledItemCss);
return(this.items=C)
},__selectPrev:function(){if(-1==this.currentSelectedItemIndex){this.currentSelectedItemIndex=this.items.length-1
}else{this.__deselectCurrentItem()
}if(this.currentSelectedItemIndex>0){this.currentSelectedItemIndex--
}else{this.currentSelectedItemIndex=this.items.length-1
}this.__selectCurrentItem()
},__selectNext:function(){if(-1!=this.currentSelectedItemIndex){this.__deselectCurrentItem()
}if(this.currentSelectedItemIndex<this.items.length-1){this.currentSelectedItemIndex++
}else{this.currentSelectedItemIndex=0
}this.__selectCurrentItem()
},__deselectCurrentItem:function(){this.__deselectByIndex(this.currentSelectedItemIndex)
},__selectCurrentItem:function(){this.__selectByIndex(this.currentSelectedItemIndex)
},__selectFirstItem:function(){this.currentSelectedItemIndex=0;
this.__selectCurrentItem()
},__selectByIndex:function(C){if(-1!=C){A.component(this.items.eq(C)).select()
}},__deselectByIndex:function(C){if(C>-1){A.component(this.items.eq(C)).unselect()
}},__openGroup:function(){var C=this.__getItemByIndex(this.currentSelectedItemIndex);
if(this.__isGroup(C)){A.component(C).show();
A.component(C).__selectFirstItem();
this.active=false
}},__closeGroup:function(){var C=this.__getItemByIndex(this.currentSelectedItemIndex);
if(this.__isGroup(C)){A.component(C).__deselectCurrentItem();
A.component(C).hide();
this.active=true
}},__returnToParentMenu:function(){var C=this.__getItemByIndex(this.currentSelectedItemIndex);
var D;
D=this.__getParentMenu()||this.__getParentMenuFromItem(C);
if(D!=null&&this.id!=A.component(D).id){this.hide();
A.component(D).popupElement.focus()
}else{this.hide()
}},__activateMenuItem:function(){var C=this.__getCurrentItem();
if(C){menuItemId=C.attr("id");
this.activateItem(menuItemId)
}},__getItemByIndex:function(C){if(C>-1){return this.items.eq(C)
}else{return null
}},__getCurrentItem:function(){return this.__getItemByIndex(this.currentSelectedItemIndex)
},__keydownHandler:function(D){var C;
if(D.keyCode){C=D.keyCode
}else{if(D.which){C=D.which
}}activeMenu=A.ui.MenuManager.getActiveSubMenu();
if(this.popup.isVisible()){switch(C){case A.KEYS.DOWN:D.preventDefault();
activeMenu.__selectNext();
break;
case A.KEYS.UP:D.preventDefault();
activeMenu.__selectPrev();
break;
case A.KEYS.LEFT:D.preventDefault();
activeMenu.__returnToParentMenu();
break;
case A.KEYS.RIGHT:D.preventDefault();
activeMenu.__openGroup();
break;
case A.KEYS.ESC:D.preventDefault();
activeMenu.__returnToParentMenu();
break;
case A.KEYS.RETURN:D.preventDefault();
activeMenu.__activateMenuItem();
break
}D.stopPropagation()
}}}
})(RichFaces.jQuery,RichFaces);;(function(C,B){B.ui=B.ui||{};
var E={rejectClass:"rf-ind-rejt",acceptClass:"rf-ind-acpt",draggingClass:"rf-ind-drag"};
var A={};
B.ui.Droppable=function(G,F){this.options={};
C.extend(this.options,A,F||{});
D.constructor.call(this,G);
this.namespace=this.namespace||"."+B.Event.createNamespace(this.name,this.id);
this.id=G;
this.parentId=this.options.parentId;
this.attachToDom(this.parentId);
this.dropElement=C(document.getElementById(this.parentId));
this.dropElement.droppable({addClasses:false});
this.dropElement.data("init",true);
B.Event.bind(this.dropElement,"drop"+this.namespace,this.drop,this);
B.Event.bind(this.dropElement,"dropover"+this.namespace,this.dropover,this);
B.Event.bind(this.dropElement,"dropout"+this.namespace,this.dropout,this)
};
B.BaseNonVisualComponent.extend(B.ui.Droppable);
var D=B.ui.Droppable.$super;
C.extend(B.ui.Droppable.prototype,(function(){return{drop:function(H){var F=H.rf.data;
if(this.accept(F.draggable)){this.__callAjax(H,F)
}var G=this.__getIndicatorObject(F.helper);
if(G){F.helper.removeClass(G.getAcceptClass());
F.helper.removeClass(G.getRejectClass())
}else{F.helper.removeClass(E.acceptClass);
F.helper.removeClass(E.rejectClass)
}},dropover:function(I){var G=I.rf.data;
var F=G.draggable;
var H=this.__getIndicatorObject(G.helper);
this.dropElement.addClass("rf-drp-hvr");
if(H){if(this.accept(F)){G.helper.removeClass(H.getRejectClass());
G.helper.addClass(H.getAcceptClass());
this.dropElement.addClass("rf-drp-hlight")
}else{G.helper.removeClass(H.getAcceptClass());
G.helper.addClass(H.getRejectClass());
this.dropElement.removeClass("rf-drp-hlight")
}}else{if(this.accept(F)){G.helper.removeClass(E.rejectClass);
G.helper.addClass(E.acceptClass);
this.dropElement.addClass("rf-drp-hlight")
}else{G.helper.removeClass(E.acceptClass);
G.helper.addClass(E.rejectClass);
this.dropElement.removeClass("rf-drp-hlight")
}}},dropout:function(I){var G=I.rf.data;
var F=G.draggable;
var H=this.__getIndicatorObject(G.helper);
this.dropElement.removeClass("rf-drp-hvr rf-drp-hlight");
if(H){G.helper.removeClass(H.getAcceptClass());
G.helper.removeClass(H.getRejectClass())
}else{G.helper.removeClass(E.acceptClass);
G.helper.removeClass(E.rejectClass)
}},accept:function(F){var H=false;
var G=F.data("type");
if(G&&this.options.acceptedTypes){C.each(this.options.acceptedTypes,function(){if(this=="@none"){return false
}if(this==G||this=="@all"){H=true;
return false
}})
}return H
},__getIndicatorObject:function(H){var G=H.attr("id");
if(G){var F=G.match(/(.*)Clone$/)[1];
return B.component(F)
}},__callAjax:function(H,G){if(G.draggable){var F=G.draggable.data("id");
var I=this.options.ajaxFunction;
if(I&&typeof I=="function"){I.call(this,H,F)
}}},destroy:function(){this.detach(this.parentId);
B.Event.unbind(this.dropElement,this.namespace);
D.destroy.call(this)
}}
})())
})(RichFaces.jQuery,window.RichFaces);;(function(E,B){B.ui=B.ui||{};
B.ui.TooltipMode={client:"client",ajax:"ajax",DEFAULT:"client"};
var A=B.ui.TooltipMode;
var D={jointPoint:"AA",direction:"AA",offset:[10,10],attached:true,mode:A.DEFAULT,hideDelay:0,hideEvent:"mouseleave",showDelay:0,showEvent:"mouseenter",followMouse:true};
var C={exec:function(H,G){var I=H.mode;
if(I==A.ajax){return this.execAjax(H,G)
}else{if(I==A.client){return this.execClient(H,G)
}else{B.log.error("SHOW_ACTION.exec : unknown mode ("+I+")")
}}},execAjax:function(H,G){H.__loading().show();
H.__content().hide();
H.__show(G);
B.ajax(H.id,null,E.extend({},H.options.ajax,{}));
return true
},execClient:function(H,G){H.__show(G)
}};
B.ui.Tooltip=B.BaseComponent.extendClass({name:"Tooltip",init:function(I,H){F.constructor.call(this,I);
this.namespace="."+B.Event.createNamespace(this.name,this.id);
this.options=E.extend(this.options,D,H||{});
this.attachToDom();
this.mode=this.options.mode;
this.target=this.options.target;
this.shown=false;
this.__addUserEventHandler("hide");
this.__addUserEventHandler("show");
this.__addUserEventHandler("beforehide");
this.__addUserEventHandler("beforeshow");
this.popupId=this.id+":wrp";
this.popup=new B.ui.Popup(this.popupId,{attachTo:this.target,attachToBody:true,positionType:"TOOLTIP",positionOffset:this.options.offset,jointPoint:this.options.jointPoint,direction:this.options.direction});
if(this.options.attached){var G={};
G[this.options.showEvent+this.namespace]=this.__showHandler;
G[this.options.hideEvent+this.namespace]=this.__hideHandler;
B.Event.bindById(this.target,G,this);
if(this.options.hideEvent=="mouseleave"){B.Event.bindById(this.popupId,this.options.hideEvent+this.namespace,this.__hideHandler,this)
}}},hide:function(){var G=this;
if(G.hidingTimerHandle){window.clearTimeout(G.hidingTimerHandle);
G.hidingTimerHandle=undefined
}if(this.shown){this.__hide()
}},__hideHandler:function(G){if(G.type=="mouseleave"&&this.__isInside(G.relatedTarget)){return 
}this.hide();
if(this.options.followMouse){B.Event.unbindById(this.target,"mousemove"+this.namespace)
}},__hide:function(){var G=this;
this.__delay(this.options.hideDelay,function(){G.__fireBeforeHide();
G.popup.hide();
G.shown=false;
G.__fireHide()
})
},__mouseMoveHandler:function(G){this.saveShowEvent=G;
if(this.shown){this.popup.show(this.saveShowEvent)
}},__showHandler:function(G){this.show(G);
var H=this;
if(H.options.followMouse){B.Event.bindById(H.target,"mousemove"+H.namespace,H.__mouseMoveHandler,H)
}},show:function(G){var H=this;
if(H.hidingTimerHandle){window.clearTimeout(H.hidingTimerHandle);
H.hidingTimerHandle=undefined
}if(!this.shown){C.exec(this,G)
}},onCompleteHandler:function(){this.__content().show();
this.__loading().hide()
},__show:function(G){var H=this;
this.__delay(this.options.showDelay,function(){if(!H.options.followMouse){H.saveShowEvent=G
}if(!H.shown){H.__fireBeforeShow();
H.popup.show(H.saveShowEvent)
}H.shown=true;
H.__fireShow()
})
},__delay:function(G,I){var H=this;
if(G>0){H.hidingTimerHandle=window.setTimeout(function(){I();
if(H.hidingTimerHandle){window.clearTimeout(H.hidingTimerHandle);
H.hidingTimerHandle=undefined
}},G)
}else{I()
}},__detectAncestorNode:function(G,H){var I=G;
while(I!=null&&I!=H){I=I.parentNode
}return(I!=null)
},__loading:function(){return E(document.getElementById(this.id+":loading"))
},__content:function(){return E(document.getElementById(this.id+":content"))
},__fireHide:function(){return B.Event.fireById(this.id,"hide",{id:this.id})
},__fireShow:function(){return B.Event.fireById(this.id,"show",{id:this.id})
},__fireBeforeHide:function(){return B.Event.fireById(this.id,"beforehide",{id:this.id})
},__fireBeforeShow:function(){return B.Event.fireById(this.id,"beforeshow",{id:this.id})
},__addUserEventHandler:function(G){var H=this.options["on"+G];
if(H){B.Event.bindById(this.id,G+this.namespace,H)
}},__contains:function(H,G){while(G){if(H==G.id){return true
}G=G.parentNode
}return false
},__isInside:function(G){return this.__contains(this.target,G)||this.__contains(this.popupId,G)
},destroy:function(){B.Event.unbindById(this.popupId,this.namespace);
B.Event.unbindById(this.target,this.namespace);
this.popup.destroy();
this.popup=null;
F.destroy.call(this)
}});
var F=B.ui.Tooltip.$super
})(RichFaces.jQuery,RichFaces);;(function(C,B){B.ui=B.ui||{};
B.ui.ListMulti=function(G,E){this.namespace=this.namespace||"."+B.Event.createNamespace(this.name,G);
var F=C.extend({},A,E);
D.constructor.call(this,G,F);
this.disabled=F.disabled
};
B.ui.List.extend(B.ui.ListMulti);
var D=B.ui.ListMulti.$super;
var A={clickRequiredToSelect:true};
C.extend(B.ui.ListMulti.prototype,(function(){return{name:"listMulti",getSelectedItems:function(){return this.list.find("."+this.selectItemCssMarker)
},removeSelectedItems:function(){var E=this.getSelectedItems();
this.removeItems(E);
return E
},__selectByIndex:function(E,H){if(!this.__isSelectByIndexValid(E)){return 
}this.index=this.__sanitizeSelectedIndex(E);
var G=this.items.eq(this.index);
if(!H){var F=this;
this.getSelectedItems().each(function(){F.unselectItem(C(this))
});
this.selectItem(G)
}else{if(this.isSelected(G)){this.unselectItem(G)
}else{this.selectItem(G)
}}}}
})())
})(RichFaces.jQuery,window.RichFaces);;(function(C,B){B.ui=B.ui||{};
var A={mode:"server",attachToBody:false,showDelay:50,hideDelay:300,verticalOffset:0,horizontalOffset:0,showEvent:"mouseover",positionOffset:[0,0],cssRoot:"ddm",cssClasses:{}};
B.ui.MenuBase=function(G,F){D.constructor.call(this,G,F);
this.id=G;
this.namespace=this.namespace||"."+B.Event.createNamespace(this.name,this.id);
this.options={};
C.extend(this.options,A,F||{});
C.extend(this.options.cssClasses,E.call(this,this.options.cssRoot));
this.attachToDom(G);
this.element=B.getDomElement(this.id);
this.displayed=false;
this.options.positionOffset=[this.options.horizontalOffset,this.options.verticalOffset];
this.popup=new RichFaces.ui.Popup(this.id+"_list",{attachTo:this.id,direction:this.options.direction,jointPoint:this.options.jointPoint,positionType:this.options.positionType,positionOffset:this.options.positionOffset,attachToBody:this.options.attachToBody});
this.selectedGroup=null;
B.Event.bindById(this.id,"mouseenter",C.proxy(this.__overHandler,this),this);
B.Event.bindById(this.id,"mouseleave",C.proxy(this.__leaveHandler,this),this);
this.popupElement=B.getDomElement(this.popup.id);
this.popupElement.tabIndex=-1;
this.__updateItemsList();
B.Event.bind(this.items,"mouseenter",C.proxy(this.__itemMouseEnterHandler,this),this);
this.currentSelectedItemIndex=-1;
var H;
H={};
H["keydown"+this.namespace]=this.__keydownHandler;
B.Event.bind(this.popupElement,H,this)
};
var E=function(G){var F={itemCss:"rf-"+G+"-itm",selectItemCss:"rf-"+G+"-itm-sel",unselectItemCss:"rf-"+G+"-itm-unsel",disabledItemCss:"rf-"+G+"-itm-dis",labelCss:"rf-"+G+"-lbl",listCss:"rf-"+G+"-lst",listContainerCss:"rf-"+G+"-lst-bg"};
return F
};
B.BaseComponent.extend(B.ui.MenuBase);
var D=B.ui.MenuBase.$super;
C.extend(B.ui.MenuBase.prototype,(function(){return{name:"MenuBase",show:function(){this.__showPopup()
},hide:function(){this.__hidePopup()
},processItem:function(F){if(F&&F.attr("id")&&!this.__isDisabled(F)&&!this.__isGroup(F)){this.invokeEvent("itemclick",B.getDomElement(this.id),null);
this.hide()
}},activateItem:function(G){var F=C(RichFaces.getDomElement(G));
B.Event.fireById(F.attr("id"),"click")
},__showPopup:function(F){if(!this.__isShown()){this.invokeEvent("show",B.getDomElement(this.id),null);
this.popup.show(F);
this.displayed=true;
B.ui.MenuManager.setActiveSubMenu(B.component(this.element))
}this.popupElement.focus()
},__hidePopup:function(){window.clearTimeout(this.showTimeoutId);
this.showTimeoutId=null;
if(this.__isShown()){this.invokeEvent("hide",B.getDomElement(this.id),null);
this.__closeChildGroups();
this.popup.hide();
this.displayed=false;
this.__deselectCurrentItem();
this.currentSelectedItemIndex=-1;
var F=B.component(this.__getParentMenu());
if(this.id!=F.id){F.popupElement.focus();
B.ui.MenuManager.setActiveSubMenu(F)
}}},__closeChildGroups:function(){var F=0;
var G;
for(F in this.items){G=this.items.eq(F);
if(this.__isGroup(G)){B.component(G).hide()
}}},__getParentMenuFromItem:function(F){var G;
if(F){G=F.parents("div."+this.options.cssClasses.itemCss).has("div."+this.options.cssClasses.listContainerCss).eq(1)
}if(G&&G.length>0){return G
}else{G=F.parents("div."+this.options.cssClasses.labelCss);
if(G&&G.length>0){return G
}else{return null
}}},__getParentMenu:function(){var G=C(this.element).parents("div."+this.options.cssClasses.itemCss).has("div."+this.options.cssClasses.listContainerCss).eq(0);
if(G&&G.length>0){return G
}else{var F=this.items.eq(0);
return this.__getParentMenuFromItem(F)
}},__isGroup:function(F){return F.find("div."+this.options.cssClasses.listCss).length>0
},__isDisabled:function(F){return F.hasClass(this.options.cssClasses.disabledItemCss)
},__isShown:function(){return this.displayed
},__itemMouseEnterHandler:function(G){var F=this.__getItemFromEvent(G);
if(F){if(this.currentSelectedItemIndex!=this.items.index(F)){this.__deselectCurrentItem();
this.currentSelectedItemIndex=this.items.index(F)
}}},__selectItem:function(F){if(!B.component(F).isSelected){B.component(F).select()
}},__getItemFromEvent:function(F){return C(F.target).closest("."+this.options.cssClasses.itemCss,F.currentTarget).eq(0)
},__showHandler:function(F){if(!this.__isShown()){this.showTimeoutId=window.setTimeout(C.proxy(function(){this.show(F)
},this),this.options.showDelay);
return false
}},__leaveHandler:function(){this.hideTimeoutId=window.setTimeout(C.proxy(function(){this.hide()
},this),this.options.hideDelay)
},__overHandler:function(){window.clearTimeout(this.hideTimeoutId);
this.hideTimeoutId=null
},destroy:function(){this.detach(this.id);
B.Event.unbind(this.popupElement,"keydown"+this.namespace);
this.popup.destroy();
this.popup=null;
D.destroy.call(this)
}}
})())
})(RichFaces.jQuery,RichFaces);;(function(C,B){B.ui=B.ui||{};
B.ui.PopupList=function(H,F,E){this.namespace=this.namespace||"."+B.Event.createNamespace(this.name,H);
var G=C.extend({},A,E);
D.constructor.call(this,H,G);
G.selectListener=F;
this.list=new B.ui.List(H,G)
};
B.ui.Popup.extend(B.ui.PopupList);
var D=B.ui.PopupList.$super;
var A={attachToBody:true,positionType:"DROPDOWN",positionOffset:[0,0]};
C.extend(B.ui.PopupList.prototype,(function(){return{name:"popupList",__getList:function(){return this.list
},destroy:function(){this.list.destroy();
this.list=null;
D.destroy.call(this)
}}
})())
})(RichFaces.jQuery,window.RichFaces);;(function(C,B){B.ui=B.ui||{};
var A={positionType:"DROPDOWN",direction:"AA",jointPoint:"AA",cssRoot:"ddm",cssClasses:{}};
B.ui.Menu=function(G,F){this.options={};
C.extend(this.options,A,F||{});
C.extend(this.options.cssClasses,E.call(this,this.options.cssRoot));
D.constructor.call(this,G,this.options);
this.id=G;
this.namespace=this.namespace||"."+B.Event.createNamespace(this.name,this.id);
this.groupList=new Array();
this.target=this.getTarget();
this.targetComponent=B.component(this.target);
if(this.target){var H=this;
C(document).ready(function(){if(H.targetComponent&&H.targetComponent.contextMenuAttach){H.targetComponent.contextMenuAttach(H);
C("body").on("rich:ready"+H.namespace,'[id="'+H.target+'"]',function(){H.targetComponent.contextMenuAttach(H)
})
}else{B.Event.bindById(H.target,H.options.showEvent,C.proxy(H.__showHandler,H),H)
}})
}this.element=C(B.getDomElement(this.id));
if(!B.ui.MenuManager){B.ui.MenuManager={}
}this.menuManager=B.ui.MenuManager
};
var E=function(G){var F={selectMenuCss:"rf-"+G+"-sel",unselectMenuCss:"rf-"+G+"-unsel"};
return F
};
B.ui.MenuBase.extend(B.ui.Menu);
var D=B.ui.Menu.$super;
C.extend(B.ui.Menu.prototype,B.ui.MenuKeyNavigation);
C.extend(B.ui.Menu.prototype,(function(){return{name:"Menu",initiateGroups:function(F){for(var H in F){var G=F[H].id;
if(null!=G){this.groupList[G]=new B.ui.MenuGroup(G,{rootMenuId:this.id,onshow:F[H].onshow,onhide:F[H].onhide,horizontalOffset:F[H].horizontalOffset,verticalOffset:F[H].verticalOffset,jointPoint:F[H].jointPoint,direction:F[H].direction,cssRoot:F[H].cssRoot})
}}},getTarget:function(){return this.id+"_label"
},show:function(F){if(this.menuManager.openedMenu!=this.id){this.menuManager.shutdownMenu();
this.menuManager.addMenuId(this.id);
this.__showPopup()
}},hide:function(){this.__hidePopup();
this.menuManager.deletedMenuId()
},select:function(){this.element.removeClass(this.options.cssClasses.unselectMenuCss);
this.element.addClass(this.options.cssClasses.selectMenuCss)
},unselect:function(){this.element.removeClass(this.options.cssClasses.selectMenuCss);
this.element.addClass(this.options.cssClasses.unselectMenuCss)
},__overHandler:function(){D.__overHandler.call(this);
this.select()
},__leaveHandler:function(){D.__leaveHandler.call(this);
this.unselect()
},destroy:function(){this.detach(this.id);
if(this.target){B.Event.unbindById(this.target,this.options.showEvent);
if(this.targetComponent&&this.targetComponent.contextMenuAttach){C("body").off("rich:ready"+this.namespace,'[id="'+this.target+'"]')
}}D.destroy.call(this)
}}
})());
B.ui.MenuManager={openedMenu:null,activeSubMenu:null,addMenuId:function(F){this.openedMenu=F
},deletedMenuId:function(){this.openedMenu=null
},shutdownMenu:function(){if(this.openedMenu!=null){B.component(B.getDomElement(this.openedMenu)).hide()
}this.deletedMenuId()
},setActiveSubMenu:function(F){this.activeSubMenu=F
},getActiveSubMenu:function(){return this.activeSubMenu
}}
})(RichFaces.jQuery,RichFaces);;(function(C,B){B.ui=B.ui||{};
var A={showEvent:"mouseenter",direction:"AA",jointPoint:"AA",positionType:"DDMENUGROUP",showDelay:300};
B.ui.MenuGroup=function(F,E){this.id=F;
this.options={};
C.extend(this.options,A,E||{});
D.constructor.call(this,F,this.options);
this.namespace=this.namespace||"."+B.Event.createNamespace(this.name,this.id);
this.attachToDom(F);
B.Event.bindById(this.id,this.options.showEvent,C.proxy(this.__showHandler,this),this);
this.rootMenu=B.component(this.options.rootMenuId);
this.shown=false;
this.jqueryElement=C(this.element)
};
B.ui.MenuBase.extend(B.ui.MenuGroup);
var D=B.ui.MenuGroup.$super;
C.extend(B.ui.MenuGroup.prototype,B.ui.MenuKeyNavigation);
C.extend(B.ui.MenuGroup.prototype,(function(){return{name:"MenuGroup",show:function(){var E=this.id;
if(this.rootMenu.groupList[E]&&!this.shown){this.rootMenu.invokeEvent("groupshow",B.getDomElement(this.rootMenu.id),null);
this.__showPopup();
this.shown=true
}},hide:function(){var E=this.rootMenu;
if(E.groupList[this.id]&&this.shown){E.invokeEvent("grouphide",B.getDomElement(E.id),null);
this.__hidePopup();
this.shown=false
}},select:function(){this.jqueryElement.removeClass(this.options.cssClasses.unselectItemCss);
this.jqueryElement.addClass(this.options.cssClasses.selectItemCss)
},unselect:function(){this.jqueryElement.removeClass(this.options.cssClasses.selectItemCss);
this.jqueryElement.addClass(this.options.cssClasses.unselectItemCss)
},__showHandler:function(){this.select();
D.__showHandler.call(this)
},__leaveHandler:function(){window.clearTimeout(this.showTimeoutId);
this.showTimeoutId=null;
this.hideTimeoutId=window.setTimeout(C.proxy(function(){this.hide()
},this),this.options.hideDelay);
this.unselect()
},destroy:function(){this.detach(this.id);
D.destroy.call(this)
}}
})())
})(RichFaces.jQuery,RichFaces);;(function(D,C){C.ui=C.ui||{};
C.ui.PickList=function(I,F){var H=D.extend({},A,F);
E.constructor.call(this,I,H);
this.namespace=this.namespace||"."+C.Event.createNamespace(this.name,this.id);
this.attachToDom();
H.scrollContainer=D(document.getElementById(I+"SourceItems"));
this.sourceList=new C.ui.ListMulti(I+"SourceList",H);
H.scrollContainer=D(document.getElementById(I+"TargetItems"));
this.selectItemCss=H.selectItemCss;
var G=I+"SelValue";
this.hiddenValues=D(document.getElementById(G));
H.hiddenId=G;
this.orderable=H.orderable;
if(this.orderable){this.orderingList=new C.ui.OrderingList(I+"Target",H);
this.targetList=this.orderingList.list
}else{this.targetList=new C.ui.ListMulti(I+"TargetList",H)
}this.pickList=D(document.getElementById(I));
this.addButton=D(".rf-pick-add",this.pickList);
this.addButton.bind("click",D.proxy(this.add,this));
this.addAllButton=D(".rf-pick-add-all",this.pickList);
this.addAllButton.bind("click",D.proxy(this.addAll,this));
this.removeButton=D(".rf-pick-rem",this.pickList);
this.removeButton.bind("click",D.proxy(this.remove,this));
this.removeAllButton=D(".rf-pick-rem-all",this.pickList);
this.removeAllButton.bind("click",D.proxy(this.removeAll,this));
this.disabled=H.disabled;
if(H.onadditems&&typeof H.onadditems=="function"){C.Event.bind(this.targetList,"additems",H.onadditems)
}C.Event.bind(this.targetList,"additems",D.proxy(this.toggleButtons,this));
this.focused=false;
this.keepingFocus=false;
B.call(this,H);
if(H.onremoveitems&&typeof H.onremoveitems=="function"){C.Event.bind(this.sourceList,"additems",H.onremoveitems)
}C.Event.bind(this.sourceList,"additems",D.proxy(this.toggleButtons,this));
C.Event.bind(this.sourceList,"selectItem",D.proxy(this.toggleButtons,this));
C.Event.bind(this.sourceList,"unselectItem",D.proxy(this.toggleButtons,this));
C.Event.bind(this.targetList,"selectItem",D.proxy(this.toggleButtons,this));
C.Event.bind(this.targetList,"unselectItem",D.proxy(this.toggleButtons,this));
if(H.switchByClick){C.Event.bind(this.sourceList,"click",D.proxy(this.add,this));
C.Event.bind(this.targetList,"click",D.proxy(this.remove,this))
}if(H.switchByDblClick){C.Event.bind(this.sourceList,"dblclick",D.proxy(this.add,this));
C.Event.bind(this.targetList,"dblclick",D.proxy(this.remove,this))
}if(F.onchange&&typeof F.onchange=="function"){C.Event.bind(this,"change"+this.namespace,F.onchange)
}D(document).ready(D.proxy(this.toggleButtons,this))
};
C.BaseComponent.extend(C.ui.PickList);
var E=C.ui.PickList.$super;
var A={defaultLabel:"",itemCss:"rf-pick-opt",selectItemCss:"rf-pick-sel",listCss:"rf-pick-lst-cord",clickRequiredToSelect:true,switchByClick:false,switchByDblClick:true,disabled:false};
var B=function(F){if(F.onsourcefocus&&typeof F.onsourcefocus=="function"){C.Event.bind(this.sourceList,"listfocus"+this.sourceList.namespace,F.onsourcefocus)
}if(F.onsourceblur&&typeof F.onsourceblur=="function"){C.Event.bind(this.sourceList,"listblur"+this.sourceList.namespace,F.onsourceblur)
}if(F.ontargetfocus&&typeof F.ontargetfocus=="function"){C.Event.bind(this.targetList,"listfocus"+this.targetList.namespace,F.ontargetfocus)
}if(F.ontargetblur&&typeof F.ontargetblur=="function"){C.Event.bind(this.targetList,"listblur"+this.targetList.namespace,F.ontargetblur)
}if(F.onfocus&&typeof F.onfocus=="function"){C.Event.bind(this,"listfocus"+this.namespace,F.onfocus)
}if(F.onblur&&typeof F.onblur=="function"){C.Event.bind(this,"listblur"+this.namespace,F.onblur)
}this.pickList.focusin(D.proxy(this.__focusHandler,this));
this.pickList.focusout(D.proxy(this.__blurHandler,this))
};
D.extend(C.ui.PickList.prototype,(function(){return{name:"pickList",defaultLabelClass:"rf-pick-dflt-lbl",getName:function(){return this.name
},getNamespace:function(){return this.namespace
},__focusHandler:function(F){if(!this.focused){this.focused=true;
C.Event.fire(this,"listfocus"+this.namespace,F);
this.originalValue=this.targetList.csvEncodeValues()
}},__blurHandler:function(F){if(this.focused){this.focused=false;
C.Event.fire(this,"listblur"+this.namespace,F)
}},getSourceList:function(){return this.sourceList
},getTargetList:function(){return this.targetList
},add:function(){this.targetList.setFocus();
var F=this.sourceList.removeSelectedItems();
this.targetList.addItems(F);
this.encodeHiddenValues()
},remove:function(){this.sourceList.setFocus();
var F=this.targetList.removeSelectedItems();
this.sourceList.addItems(F);
this.encodeHiddenValues()
},addAll:function(){this.targetList.setFocus();
var F=this.sourceList.removeAllItems();
this.targetList.addItems(F);
this.encodeHiddenValues()
},removeAll:function(){this.sourceList.setFocus();
var F=this.targetList.removeAllItems();
this.sourceList.addItems(F);
this.encodeHiddenValues()
},encodeHiddenValues:function(){var F=this.hiddenValues.val();
var G=this.targetList.csvEncodeValues();
if(F!==G){this.hiddenValues.val(G)
}C.Event.fire(this,"change"+this.namespace,{oldValues:F,newValues:G})
},toggleButtons:function(){this.__toggleButton(this.addButton,this.sourceList.__getItems().filter("."+this.selectItemCss).length>0);
this.__toggleButton(this.removeButton,this.targetList.__getItems().filter("."+this.selectItemCss).length>0);
this.__toggleButton(this.addAllButton,this.sourceList.__getItems().length>0);
this.__toggleButton(this.removeAllButton,this.targetList.__getItems().length>0);
if(this.orderable){this.orderingList.toggleButtons()
}},__toggleButton:function(G,F){if(this.disabled||!F){if(!G.hasClass("rf-pick-btn-dis")){G.addClass("rf-pick-btn-dis")
}if(!G.attr("disabled")){G.attr("disabled",true)
}}else{if(G.hasClass("rf-pick-btn-dis")){G.removeClass("rf-pick-btn-dis")
}if(G.attr("disabled")){G.attr("disabled",false)
}}}}
})())
})(RichFaces.jQuery,window.RichFaces);;(function(C,B){B.ui=B.ui||{};
B.ui.InplaceInput=function(J,F){var I=C.extend({},A,F);
D.constructor.call(this,J,I);
this.label=C(document.getElementById(J+"Label"));
var G=this.label.text();
var H=this.__getValue();
this.initialLabel=(G==H)?G:"";
this.useDefaultLabel=G!=H;
this.saveOnBlur=I.saveOnBlur;
this.showControls=I.showControls;
this.getInput().bind("focus",C.proxy(this.__editHandler,this));
if(this.showControls){var E=document.getElementById(J+"Btn");
if(E){E.tabIndex=-1
}this.okbtn=C(document.getElementById(J+"Okbtn"));
this.cancelbtn=C(document.getElementById(J+"Cancelbtn"));
this.okbtn.bind("mousedown",C.proxy(this.__saveBtnHandler,this));
this.cancelbtn.bind("mousedown",C.proxy(this.__cancelBtnHandler,this))
}};
B.ui.InplaceBase.extend(B.ui.InplaceInput);
var D=B.ui.InplaceInput.$super;
var A={defaultLabel:"",saveOnBlur:true,showControl:true,noneCss:"rf-ii-none",readyCss:"rf-ii",editCss:"rf-ii-act",changedCss:"rf-ii-chng"};
C.extend(B.ui.InplaceInput.prototype,(function(){return{name:"inplaceInput",defaultLabelClass:"rf-ii-dflt-lbl",getName:function(){return this.name
},getNamespace:function(){return this.namespace
},__keydownHandler:function(E){this.tabBlur=false;
switch(E.keyCode||E.which){case B.KEYS.ESC:E.preventDefault();
this.cancel();
this.onblur(E);
break;
case B.KEYS.RETURN:E.preventDefault();
this.save();
this.onblur(E);
break;
case B.KEYS.TAB:this.tabBlur=true;
break
}},__blurHandler:function(E){this.onblur(E)
},__isSaveOnBlur:function(){return this.saveOnBlur
},__setInputFocus:function(){this.getInput().unbind("focus",this.__editHandler);
this.getInput().focus()
},__saveBtnHandler:function(E){this.cancelButton=false;
this.save();
this.onblur(E)
},__cancelBtnHandler:function(E){this.cancelButton=true;
this.cancel();
this.onblur(E)
},__editHandler:function(E){D.__editHandler.call(this,E);
this.onfocus(E)
},getLabel:function(){return this.label.text()
},setLabel:function(E){this.label.text(E);
if(E==this.defaultLabel){this.label.addClass(this.defaultLabelClass)
}else{this.label.removeClass(this.defaultLabelClass)
}},isValueChanged:function(){return(this.__getValue()!=this.initialLabel)
},onshow:function(){this.__setInputFocus()
},onhide:function(){if(this.tabBlur){this.tabBlur=false
}else{this.getInput().focus()
}},onfocus:function(E){if(!this.__isFocused()){this.__setFocused(true);
this.focusValue=this.__getValue();
this.invokeEvent.call(this,"focus",document.getElementById(this.id),E)
}},onblur:function(E){if(this.__isFocused()){this.__setFocused(false);
this.invokeEvent.call(this,"blur",document.getElementById(this.id),E);
if(this.isValueSaved()||this.__isSaveOnBlur()){this.save()
}else{this.cancel()
}this.__hide();
if(!this.cancelButton){if(this.__isValueChanged()){this.invokeEvent.call(this,"change",document.getElementById(this.id),E)
}}var F=this;
window.setTimeout(function(){F.getInput().bind("focus",C.proxy(F.__editHandler,F))
},1)
}},__isValueChanged:function(){return(this.focusValue!=this.__getValue())
},__setFocused:function(E){this.focused=E
},__isFocused:function(){return this.focused
},setValue:function(E){this.__setValue(E);
this.save()
}}
})())
})(RichFaces.jQuery,window.RichFaces);;(function(C,B){B.ui=B.ui||{};
var A={showEvent:"contextmenu",cssRoot:"ctx",cssClasses:{},attached:true};
B.ui.ContextMenu=function(F,E){this.options={};
C.extend(this.options,A,E||{});
D.constructor.call(this,F,this.options);
this.id=F;
this.namespace=this.namespace||"."+B.Event.createNamespace(this.name,this.id);
B.Event.bind("body","click"+this.namespace,C.proxy(this.__leaveHandler,this));
B.Event.bindById(this.id,"click"+this.namespace,C.proxy(this.__clilckHandler,this))
};
B.ui.Menu.extend(B.ui.ContextMenu);
var D=B.ui.ContextMenu.$super;
C.extend(B.ui.ContextMenu.prototype,(function(){return{name:"ContextMenu",getTarget:function(){if(!this.options.attached){return null
}var E=typeof this.options.target==="undefined"?this.element.parentNode.id:this.options.target;
return E
},__showHandler:function(E){if(this.__isShown()){this.hide()
}return D.__showHandler.call(this,E)
},show:function(F){if(this.menuManager.openedMenu!=this.id){this.menuManager.shutdownMenu();
this.menuManager.addMenuId(this.id);
this.__showPopup(F);
var E=B.component(this.target);
if(E&&E.contextMenuShow){E.contextMenuShow(this,F)
}}},__clilckHandler:function(E){E.preventDefault();
E.stopPropagation()
},destroy:function(){B.Event.unbind("body","click"+this.namespace);
B.Event.unbindById(this.id,"click"+this.namespace);
D.destroy.call(this)
}}
})())
})(RichFaces.jQuery,RichFaces);;(function(D,C){C.ui=C.ui||{};
C.ui.Select=function(L,H){this.id=L;
this.element=this.attachToDom();
var K=D.extend({},B,H);
K.attachTo=L;
K.scrollContainer=D(document.getElementById(L+"Items")).parent()[0];
K.focusKeeperEnabled=false;
E.constructor.call(this,L,K);
this.options=K;
this.defaultLabel=K.defaultLabel;
var J=this.__getValue();
this.initialValue=(J!=this.defaultLabel)?J:"";
this.selValueInput=D(document.getElementById(L+"selValue"));
this.container=this.selValueInput.parent();
this.clientSelectItems=K.clientSelectItems;
this.filterFunction=K.filterFunction;
if(K.showControl&&!K.disabled){this.container.bind("mousedown",D.proxy(this.__onBtnMouseDown,this)).bind("mouseup",D.proxy(this.__onMouseUp,this))
}this.isFirstAjax=true;
this.previousValue=this.__getValue();
this.selectFirst=K.selectFirst;
this.popupList=new C.ui.PopupList((L+"List"),this,K);
this.list=this.popupList.__getList();
this.listElem=D(document.getElementById(L+"List"));
this.listElem.bind("mousedown",D.proxy(this.__onListMouseDown,this));
this.listElem.bind("mouseup",D.proxy(this.__onMouseUp,this));
var I={};
I["listshow"+this.namespace]=D.proxy(this.__listshowHandler,this);
I["listhide"+this.namespace]=D.proxy(this.__listhideHandler,this);
I["change"+this.namespace]=D.proxy(this.__onInputChangeHandler,this);
C.Event.bind(this.element,I,this);
this.originalItems=this.list.__getItems();
this.enableManualInput=K.enableManualInput||K.isAutocomplete;
if(this.enableManualInput){F.call(this,"",this.clientSelectItems)
}this.changeDelay=K.changeDelay
};
C.ui.InputBase.extend(C.ui.Select);
var E=C.ui.Select.$super;
var B={defaultLabel:"",selectFirst:true,showControl:true,enableManualInput:false,itemCss:"rf-sel-opt",selectItemCss:"rf-sel-sel",listCss:"rf-sel-lst-cord",changeDelay:8,disabled:false,filterFunction:undefined,isAutocomplete:false,ajaxMode:true,lazyClientMode:false,isCachedAjax:true};
var G=/^[\n\s]*(.*)[\n\s]*$/;
var F=function(I,H){if(!H){H=[]
}if(H.length||(!this.options.isAutocomplete&&!this.options.isCachedAjax)){this.clientSelectItems=H
}this.originalItems=this.list.__updateItemsList();
this.list.__storeClientSelectItems(H);
if(this.originalItems.length>0){this.cache=new C.utils.Cache((this.options.ajaxMode?I:""),this.originalItems,A,!this.options.ajaxMode)
}};
var A=function(H){var I=[];
H.each(function(){I.push(D(this).text().replace(G,"$1"))
});
return I
};
D.extend(C.ui.Select.prototype,(function(){return{name:"select",defaultLabelClass:"rf-sel-dflt-lbl",__listshowHandler:function(H){if(this.originalItems.length==0&&this.isFirstAjax){this.callAjax(H)
}},__listhideHandler:function(H){},__onInputChangeHandler:function(H){this.__setValue(this.input.val())
},__onBtnMouseDown:function(H){if(!this.popupList.isVisible()&&!this.options.isAutocomplete){this.__updateItems();
this.__showPopup()
}else{this.__hidePopup()
}this.isMouseDown=true
},__focusHandler:function(H){if(!this.focused){if(this.__getValue()==this.defaultLabel){this.__setValue("")
}this.focusValue=this.selValueInput.val();
this.focused=true;
this.invokeEvent.call(this,"focus",document.getElementById(this.id),H)
}},__keydownHandler:function(I){var H;
if(I.keyCode){H=I.keyCode
}else{if(I.which){H=I.which
}}var J=this.popupList.isVisible();
switch(H){case C.KEYS.DOWN:I.preventDefault();
I.stopPropagation();
if(!J){this.__updateItems();
this.__showPopup()
}else{this.list.__selectNext()
}break;
case C.KEYS.UP:I.preventDefault();
I.stopPropagation();
if(J){this.list.__selectPrev()
}break;
case C.KEYS.TAB:case C.KEYS.RETURN:if(H==C.KEYS.TAB&&!J){break
}I.preventDefault();
if(J){this.list.__selectCurrent()
}return false;
break;
case C.KEYS.ESC:I.preventDefault();
if(J){this.__hidePopup()
}break;
default:if(this.__selectItemByLabel(H)){break
}var K=this;
window.clearTimeout(this.changeTimerId);
this.changeTimerId=window.setTimeout(function(){K.__onChangeValue(I)
},this.changeDelay);
break
}},__onChangeValue:function(I){var H=this.__getValue();
if(H===this.previousValue){return 
}this.previousValue=H;
if(!this.options.isAutocomplete||(this.options.isCachedAjax||!this.options.ajaxMode)&&this.cache&&this.cache.isCached(H)){this.__updateItems();
if(this.isAutocomplete){this.originalItems=this.list.__getItems()
}if(this.list.__getItems().length!=0){this.container.removeClass("rf-sel-fld-err")
}else{this.container.addClass("rf-sel-fld-err")
}if(!this.popupList.isVisible()){this.__showPopup()
}}else{if(H.length>=this.options.minChars){if((this.options.ajaxMode||this.options.lazyClientMode)){this.callAjax(I)
}}else{if(this.options.ajaxMode){this.clearItems();
this.__hidePopup()
}}}},clearItems:function(){this.list.removeAllItems()
},callAjax:function(K){var M=this;
var H=K;
var J=function(N){F.call(M,M.__getValue(),N.componentData&&N.componentData[M.id]);
if(M.clientSelectItems&&M.clientSelectItems.length){M.__updateItems();
M.__showPopup()
}else{M.__hidePopup()
}};
var I=function(N){M.__hidePopup();
M.clearItems()
};
this.isFirstAjax=false;
var L={};
L[this.id+".ajax"]="1";
C.ajax(this.id,K,{parameters:L,error:I,complete:J})
},__blurHandler:function(I){if(!this.isMouseDown){var H=this;
this.timeoutId=window.setTimeout(function(){if(H.input!==null){H.onblur(I)
}},200)
}else{this.__setInputFocus();
this.isMouseDown=false
}},__onListMouseDown:function(H){this.isMouseDown=true
},__onMouseUp:function(H){this.isMouseDown=false;
this.__setInputFocus()
},__updateItems:function(){var H=this.__getValue();
H=(H!=this.defaultLabel)?H:"";
this.__updateItemsFromCache(H);
if(this.selectFirst&&this.enableManualInput&&!this.__isValueSelected(H)){this.list.__selectByIndex(0)
}},__updateItemsFromCache:function(J){if(this.originalItems.length>0&&(this.enableManualInput||this.isAutocomplete)&&!this.__isValueSelected(J)){var I=this.cache.getItems(J,this.filterFunction);
var H=D(I);
this.list.__unselectPrevious();
this.list.__setItems(H);
D(document.getElementById(this.id+"Items")).children().detach();
D(document.getElementById(this.id+"Items")).append(H)
}},__getClientItemFromCache:function(K){var J;
var I;
if(this.enableManualInput){var H=this.cache.getItems(K,this.filterFunction);
if(H&&H.length>0){var L=D(H[0]);
D.each(this.clientSelectItems,function(){if(this.id==L.attr("id")){I=this.label;
J=this.value;
return false
}})
}else{I=K;
J=""
}}if(I){return{label:I,value:J}
}},__getClientItem:function(J){var I;
var H=J;
D.each(this.clientSelectItems,function(){if(H==this.label){I=this.value
}});
if(H&&I){return{label:H,value:I}
}},__isValueSelected:function(H){var I=this.__getClientItemFromCache(H);
return I.label===H&&I.value==this.getValue()
},__selectItemByLabel:function(J){if(this.enableManualInput||J<48||(J>57&&J<65)||J>90){return false
}if(!this.popupList.isVisible()){this.__updateItems();
this.__showPopup()
}var H=new Array();
D.each(this.clientSelectItems,function(K){if(this.label[0].toUpperCase().charCodeAt(0)==J){H.push(K)
}});
if(H.length){var I=0;
if(this.lastKeyCode&&this.lastKeyCode==J){I=this.lastKeyCodeCount+1;
if(I>=H.length){I=0
}}this.lastKeyCode=J;
this.lastKeyCodeCount=I;
this.list.__selectByIndex(H[I])
}return false
},__showPopup:function(){if(this.originalItems.length>0){this.popupList.show();
if(!this.options.enableManualInput||this.__isValueSelected(this.getLabel())){if(this.originalItems.length>this.popupList.list.items.length){this.popupList.list.__unselectPrevious();
this.popupList.list.__setItems(this.originalItems);
D(document.getElementById(this.id+"Items")).children().detach();
D(document.getElementById(this.id+"Items")).append(this.originalItems)
}this.list.__selectItemByValue(this.getValue())
}}this.invokeEvent.call(this,"listshow",document.getElementById(this.id))
},__hidePopup:function(){this.popupList.hide();
this.invokeEvent.call(this,"listhide",document.getElementById(this.id))
},showPopup:function(){if(!this.popupList.isVisible()){this.__updateItems();
this.__showPopup()
}this.__setInputFocus();
if(!this.focused){if(this.__getValue()==this.defaultLabel){this.__setValue("")
}this.focusValue=this.selValueInput.val();
this.focused=true;
this.invokeEvent.call(this,"focus",document.getElementById(this.id))
}},hidePopup:function(){if(this.popupList.isVisible()){this.__hidePopup();
var H=this.__getValue();
if(!H||H==""){this.__setValue(this.defaultLabel);
this.selValueInput.val("")
}this.focused=false;
this.invokeEvent.call(this,"blur",document.getElementById(this.id));
if(this.focusValue!=this.selValueInput.val()){this.invokeEvent.call(this,"change",document.getElementById(this.id))
}}},processItem:function(J){var I=D(J).attr("id");
var H,K;
D.each(this.clientSelectItems,function(){if(this.id==I){H=this.label;
K=this.value;
return false
}});
this.__setValue(H);
this.selValueInput.val(K);
this.__hidePopup();
this.__setInputFocus();
this.invokeEvent.call(this,"selectitem",document.getElementById(this.id))
},__save:function(){var J="";
var H="";
var I=this.__getValue();
var K;
if(I&&I!=""){if(this.enableManualInput){K=this.__getClientItemFromCache(I)
}else{K=this.__getClientItem(I)
}if(K){H=K.label;
J=K.value
}}this.__setValue(H);
this.selValueInput.val(J)
},onblur:function(I){this.__hidePopup();
var H=this.__getValue();
if(!H||H==""){this.__setValue(this.defaultLabel);
this.selValueInput.val("")
}this.focused=false;
this.invokeEvent.call(this,"blur",document.getElementById(this.id),I);
if(this.focusValue!=this.selValueInput.val()){this.invokeEvent.call(this,"change",document.getElementById(this.id),I)
}},getValue:function(){return this.selValueInput.val()
},setValue:function(J){if(J==null||J==""){this.__setValue("");
this.__save();
this.__updateItems();
return 
}var I;
for(var H=0;
H<this.clientSelectItems.length;
H++){I=this.clientSelectItems[H];
if(I.value==J){this.__setValue(I.label);
this.__save();
this.list.__selectByIndex(H);
return 
}}},getLabel:function(){return this.__getValue()
},destroy:function(){this.popupList.destroy();
this.popupList=null;
E.destroy.call(this)
}}
})());
C.csv=C.csv||{};
C.csv.validateSelectLabelValue=function(H,N,M,L){var J=D(document.getElementById(N+"selValue")).val();
var I=D(document.getElementById(N+"Input")).val();
var K=RichFaces.component(N).defaultLabel;
if(!J&&I&&(I!=K)){throw C.csv.getMessage(null,"UISELECTONE_INVALID",[N,""])
}}
})(RichFaces.jQuery,window.RichFaces);;(function(D,C){C.ui=C.ui||{};
C.ui.OrderingList=function(I,F){var H=D.extend({},A,F);
E.constructor.call(this,I,H);
this.namespace=this.namespace||"."+C.Event.createNamespace(this.name,this.id);
this.attachToDom();
H.scrollContainer=D(document.getElementById(I+"Items"));
this.orderingList=D(document.getElementById(I));
this.list=new C.ui.ListMulti(I+"List",H);
var G=H.hiddenId===null?I+"SelValue":H.hiddenId;
this.hiddenValues=D(document.getElementById(G));
this.selectItemCss=H.selectItemCss;
this.disabled=H.disabled;
this.upButton=D(".rf-ord-up",this.orderingList);
this.upButton.bind("click",D.proxy(this.up,this));
this.upTopButton=D(".rf-ord-up-tp",this.orderingList);
this.upTopButton.bind("click",D.proxy(this.upTop,this));
this.downButton=D(".rf-ord-dn",this.orderingList);
this.downButton.bind("click",D.proxy(this.down,this));
this.downBottomButton=D(".rf-ord-dn-bt",this.orderingList);
this.downBottomButton.bind("click",D.proxy(this.downBottom,this));
this.focused=false;
this.keepingFocus=false;
B.call(this,H);
if(H.onmoveitems&&typeof H.onmoveitems=="function"){C.Event.bind(this.list,"moveitems",H.onmoveitems)
}C.Event.bind(this.list,"moveitems",D.proxy(this.toggleButtons,this));
C.Event.bind(this.list,"selectItem",D.proxy(this.toggleButtons,this));
C.Event.bind(this.list,"unselectItem",D.proxy(this.toggleButtons,this));
C.Event.bind(this.list,"keydown"+this.list.namespace,D.proxy(this.__keydownHandler,this));
if(F.onchange&&typeof F.onchange=="function"){C.Event.bind(this,"change"+this.namespace,F.onchange)
}D(document).ready(D.proxy(this.toggleButtons,this))
};
C.BaseComponent.extend(C.ui.OrderingList);
var E=C.ui.OrderingList.$super;
var A={defaultLabel:"",itemCss:"rf-ord-opt",selectItemCss:"rf-ord-sel",listCss:"rf-ord-lst-cord",clickRequiredToSelect:true,disabled:false,hiddenId:null};
var B=function(G){if(G.onfocus&&typeof G.onfocus=="function"){C.Event.bind(this,"listfocus"+this.namespace,G.onfocus)
}if(G.onblur&&typeof G.onblur=="function"){C.Event.bind(this,"listblur"+this.namespace,G.onblur)
}var F={};
F["listfocus"+this.list.namespace]=D.proxy(this.__focusHandler,this);
F["listblur"+this.list.namespace]=D.proxy(this.__blurHandler,this);
C.Event.bind(this.list,F,this);
F={};
F["focus"+this.namespace]=D.proxy(this.__focusHandler,this);
F["blur"+this.namespace]=D.proxy(this.__blurHandler,this);
C.Event.bind(this.upButton,F,this);
C.Event.bind(this.upTopButton,F,this);
C.Event.bind(this.downButton,F,this);
C.Event.bind(this.downBottomButton,F,this)
};
D.extend(C.ui.OrderingList.prototype,(function(){return{name:"ordList",defaultLabelClass:"rf-ord-dflt-lbl",getName:function(){return this.name
},getNamespace:function(){return this.namespace
},__focusHandler:function(F){this.keepingFocus=this.focused;
if(!this.focused){this.focused=true;
C.Event.fire(this,"listfocus"+this.namespace,F)
}},__blurHandler:function(G){var F=this;
this.timeoutId=window.setTimeout(function(){if(!F.keepingFocus){F.focused=false;
C.Event.fire(F,"listblur"+F.namespace,G)
}F.keepingFocus=false
},200)
},__keydownHandler:function(G){if(G.isDefaultPrevented()){return 
}if(!G.metaKey){return 
}var F;
if(G.keyCode){F=G.keyCode
}else{if(G.which){F=G.which
}}switch(F){case C.KEYS.DOWN:G.preventDefault();
this.down();
break;
case C.KEYS.UP:G.preventDefault();
this.up();
break;
case C.KEYS.HOME:G.preventDefault();
this.upTop();
break;
case C.KEYS.END:G.preventDefault();
this.downBottom();
break;
default:break
}return 
},getList:function(){return this.list
},up:function(){this.keepingFocus=true;
this.list.setFocus();
var F=this.list.getSelectedItems();
this.list.move(F,-1);
this.encodeHiddenValues()
},down:function(){this.keepingFocus=true;
this.list.setFocus();
var F=this.list.getSelectedItems();
this.list.move(F,1);
this.encodeHiddenValues()
},upTop:function(){this.keepingFocus=true;
this.list.setFocus();
var G=this.list.getSelectedItems();
var F=this.list.items.index(G.first());
this.list.move(G,-F);
this.encodeHiddenValues()
},downBottom:function(){this.keepingFocus=true;
this.list.setFocus();
var G=this.list.getSelectedItems();
var F=this.list.items.index(G.last());
this.list.move(G,(this.list.items.length-1)-F);
this.encodeHiddenValues()
},encodeHiddenValues:function(){var F=this.hiddenValues.val();
var G=this.list.csvEncodeValues();
if(F!==G){this.hiddenValues.val(G);
C.Event.fire(this,"change"+this.namespace,{oldValues:F,newValues:G})
}},toggleButtons:function(){var F=this.list.__getItems();
if(this.disabled||this.list.getSelectedItems().length===0){this.__disableButton(this.upButton);
this.__disableButton(this.upTopButton);
this.__disableButton(this.downButton);
this.__disableButton(this.downBottomButton)
}else{if(this.list.items.index(this.list.getSelectedItems().first())===0){this.__disableButton(this.upButton);
this.__disableButton(this.upTopButton)
}else{this.__enableButton(this.upButton);
this.__enableButton(this.upTopButton)
}if(this.list.items.index(this.list.getSelectedItems().last())===(this.list.items.length-1)){this.__disableButton(this.downButton);
this.__disableButton(this.downBottomButton)
}else{this.__enableButton(this.downButton);
this.__enableButton(this.downBottomButton)
}}},__disableButton:function(F){if(!F.hasClass("rf-ord-btn-dis")){F.addClass("rf-ord-btn-dis")
}if(!F.attr("disabled")){F.attr("disabled",true)
}},__enableButton:function(F){if(F.hasClass("rf-ord-btn-dis")){F.removeClass("rf-ord-btn-dis")
}if(F.attr("disabled")){F.attr("disabled",false)
}}}
})())
})(RichFaces.jQuery,window.RichFaces);;(function(C,B){B.ui=B.ui||{};
B.ui.InplaceSelect=function(G,E){var F=C.extend({},A,E);
D.constructor.call(this,G,F);
this.getInput().bind("click",C.proxy(this.__clickHandler,this));
F.attachTo=G;
F.scrollContainer=C(document.getElementById(G+"Items")).parent()[0];
F.focusKeeperEnabled=false;
this.popupList=new B.ui.PopupList(G+"List",this,F);
this.list=this.popupList.__getList();
this.clientSelectItems=F.clientSelectItems;
this.selValueInput=C(document.getElementById(G+"selValue"));
this.initialValue=this.selValueInput.val();
this.listHandler=C(document.getElementById(G+"List"));
this.listHandler.bind("mousedown",C.proxy(this.__onListMouseDown,this));
this.listHandler.bind("mouseup",C.proxy(this.__onListMouseUp,this));
this.openOnEdit=F.openOnEdit;
this.saveOnSelect=F.saveOnSelect;
this.savedIndex=-1;
this.inputItem=C(document.getElementById(G+"Input"));
this.inputItemWidth=this.inputItem.width();
this.inputWidthDefined=E.inputWidth!==undefined
};
B.ui.InplaceInput.extend(B.ui.InplaceSelect);
var D=B.ui.InplaceSelect.$super;
var A={defaultLabel:"",saveOnSelect:true,openOnEdit:true,showControl:false,itemCss:"rf-is-opt",selectItemCss:"rf-is-sel",listCss:"rf-is-lst-cord",noneCss:"rf-is-none",editCss:"rf-is-fld-cntr",changedCss:"rf-is-chng"};
C.extend(B.ui.InplaceSelect.prototype,(function(){return{name:"inplaceSelect",defaultLabelClass:"rf-is-dflt-lbl",getName:function(){return this.name
},getNamespace:function(){return this.namespace
},onshow:function(){D.onshow.call(this);
if(this.openOnEdit){this.__showPopup();
this.list.__scrollToSelectedItem()
}},onhide:function(){this.__hidePopup()
},showPopup:function(){D.__show.call(this)
},__showPopup:function(){this.popupList.show();
this.__hideLabel()
},hidePopup:function(){D.__hide.call(this)
},__hidePopup:function(){this.popupList.hide();
this.__showLabel()
},onsave:function(){var G=this.list.currentSelectItem();
if(G){var F=this.list.getSelectedItemIndex();
var H=this.list.getClientSelectItemByIndex(F);
var E=H.label;
if(E==this.__getValue()){this.savedIndex=F;
this.saveItemValue(H.value);
this.list.__selectByIndex(this.savedIndex)
}else{this.list.__selectItemByValue(this.getValue())
}}},oncancel:function(){var E=this.list.getClientSelectItemByIndex(this.savedIndex);
var F=E&&E.value?E.value:this.initialValue;
this.saveItemValue(F);
this.list.__selectItemByValue(F)
},onblur:function(E){this.__hidePopup();
D.onblur.call(this)
},onfocus:function(E){if(!this.__isFocused()){this.__setFocused(true);
this.focusValue=this.selValueInput.val();
this.invokeEvent.call(this,"focus",document.getElementById(this.id),E)
}},processItem:function(F){var E=C(F).data("clientSelectItem").label;
this.__setValue(E);
this.__setInputFocus();
this.__hidePopup();
if(this.saveOnSelect){this.save()
}this.invokeEvent.call(this,"selectitem",document.getElementById(this.id))
},saveItemValue:function(E){this.selValueInput.val(E)
},__isValueChanged:function(){return(this.focusValue!=this.selValueInput.val())
},__keydownHandler:function(F){var E;
if(F.keyCode){E=F.keyCode
}else{if(F.which){E=F.which
}}if(this.popupList.isVisible()){switch(E){case B.KEYS.DOWN:F.preventDefault();
this.list.__selectNext();
this.__setInputFocus();
break;
case B.KEYS.UP:F.preventDefault();
this.list.__selectPrev();
this.__setInputFocus();
break;
case B.KEYS.RETURN:F.preventDefault();
this.list.__selectCurrent();
this.__setInputFocus();
return false;
break
}}D.__keydownHandler.call(this,F)
},__blurHandler:function(E){if(this.saveOnSelect||!this.isMouseDown){if(this.isEditState()){this.timeoutId=window.setTimeout(C.proxy(function(){this.onblur(E)
},this),200)
}}else{this.__setInputFocus();
this.isMouseDown=false
}},__clickHandler:function(E){this.__showPopup()
},__onListMouseDown:function(E){this.isMouseDown=true
},__onListMouseUp:function(E){this.isMouseDown=false;
this.__setInputFocus()
},__showLabel:function(E){this.label.show();
this.editContainer.css("position","absolute");
this.inputItem.width(this.inputItemWidth)
},__hideLabel:function(E){this.label.hide();
this.editContainer.css("position","static");
if(!this.inputWidthDefined){this.inputItem.width(this.label.width())
}},getValue:function(){return this.selValueInput.val()
},setValue:function(F){var E=this.list.__selectItemByValue(F);
var G=E.data("clientSelectItem");
this.__setValue(G.label);
if(this.__isValueChanged()){this.save();
this.invokeEvent.call(this,"change",document.getElementById(this.id))
}},destroy:function(){this.popupList.destroy();
this.popupList=null;
D.destroy.call(this)
}}
})())
})(RichFaces.jQuery,window.RichFaces);;