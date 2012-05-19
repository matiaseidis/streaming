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
<script>
var p=flowplayer('player', 'flowplayer/flowplayer-3.2.10.swf', {
clip: {
// url: 'test/luther-3.flv',
    autoPlay:false,
    autoBuffering:true,
    scaling:'fit',
    url: 'testest2Meta.flv',
	provider: 'lighthttpd'
},
plugins: {
	lighthttpd: {
		url: 'flowplayer/flowplayer.pseudostreaming/flowplayer.pseudostreaming-3.2.9.swf'
	},
    controls: {
        url: 'flowplayer/flowplayer.controls-3.2.10.swf',
        autoHide: 'never'
    }

}
} );
</script>
</body>
</html>