(function(D,I){I.ui=I.ui||{};
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
}(RichFaces.jQuery,window.RichFaces));