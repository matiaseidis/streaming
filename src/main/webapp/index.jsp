<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
<script type='text/javascript' src='flowplayer/flowplayer-3.2.9.min.js'></script>
<div style='padding-left: 50px;'><a style='display:block;width:520px;height:330px' id='player'> </a></div>
<%
String file = request.getParameter("file");
if(file != null){
	request.getSession(true).setAttribute("file", file);
}
%>

<script>
var p=flowplayer('player', 'flowplayer/flowplayer-3.2.10.swf', {
	log: { level: 'debug', filter: 'org.flowplayer.captions.*' },
clip: {
// url: 'test/luther-3.flv',
    autoPlay:false,
    autoBuffering:true,
    scaling:'fit',
    url: 'stream/sandonga.mp4',
    captionUrl: 'Luther-02x01-SP.srt',
	provider: 'lighthttpd'
},
plugins: {
	lighthttpd: {
		url: 'flowplayer/flowplayer.pseudostreaming/flowplayer.pseudostreaming-3.2.9.swf'
	},
    controls: {
        url: 'flowplayer/flowplayer.controls-3.2.10.swf',
        autoHide: 'never'
    },
    captions: {
        url: "http://releases.flowplayer.org/swf/flowplayer.captions-3.2.8.swf",

        // pointer to a content plugin (see below)
        captionTarget: 'content'
    },

    // configure a content plugin so that it
    // looks good for showing subtitles
    content: {
        url: "http://releases.flowplayer.org/swf/flowplayer.content-3.2.8.swf",
        bottom: 25,
        height:40,
        backgroundColor: 'transparent',
        backgroundGradient: 'none',
        border: 0,
        textDecoration: 'outline',
        style: {
            body: {
                fontSize: 14,
                fontFamily: 'Arial',
                textAlign: 'center',
                color: '#ffffff'
            }
        }
    }

}
} );
</script>
</body>
</html>