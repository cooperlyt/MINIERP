(function(D,B,A){B.push={options:{transport:"websocket",fallbackTransport:"long-polling",logLevel:"info"},_subscribedTopics:{},_addedTopics:{},_removedTopics:{},_handlersCounter:{},_pushSessionId:null,_lastMessageNumber:-1,_pushResourceUrl:null,_pushHandlerUrl:null,updateConnection:function(){if(D.isEmptyObject(this._handlersCounter)){this._disconnect()
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
}}(RichFaces.jQuery,RichFaces,jsf));