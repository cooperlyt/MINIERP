(function(A,B){if(typeof define==="function"&&define.amd){define(B)
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
}));