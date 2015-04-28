(function(G,E,A){E.ajaxContainer=E.ajaxContainer||{};
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
}(RichFaces.jQuery,RichFaces,jsf));