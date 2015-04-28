(function(A){if(typeof define==="function"&&define.amd){define(["jquery"],A)
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
}));