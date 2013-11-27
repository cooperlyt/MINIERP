//<![CDATA[

function popPrintPage(contextPath,reportPath) {
	if (reportPath != '')
	window
			.open(
					contextPath + '/frameset?__report='
							+ reportPath
							+ '&__dpi=96&__format=pdf&__pageoverflow=0&__overwrite=true&__showtitle=false&__toolbar=false?rnd='
							+ new Date().getTime(),
					'newwindow',
					' top=0,left=0, toolbar=no, menubar=no,location=no, status=no,resizable yes,fullscreen yes')
}

function popPrintPageView(contextPath,reportPath) {
	if (reportPath != '')
	window
			.open(
					contextPath + '/frameset?__report='
							+ reportPath
							+ '&__locale=zh_CN&__dpi=96&__overwrite=true&__showtitle=false&__toolbar=true?rnd='
							+ new Date().getTime(),
					'newwindow',
					' top=0,left=0, toolbar=no, menubar=no,location=no')
}


// ]]>
