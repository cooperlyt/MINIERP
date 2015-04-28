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
}}(RichFaces.jQuery,RichFaces));