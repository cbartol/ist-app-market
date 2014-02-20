function toggleLikeComment(commentId) {
	myJsRoutes.controllers.AppController.toggleLikeOnComment(commentId).ajax({
	    success : function(data) {
	    	$('#comment_'+commentId).replaceWith(data);
	    }
	});
};

function addComment(appId) {
	var r = myJsRoutes.controllers.AppController.addComment(appId);
	$.ajax({
		url: r.url,
		type: r.type,
		data: {
			text: document.getElementById("addComment").value
		},
		success: function(tpl) {
			$('#comment-list').prepend(tpl);
		},
		error: function(err) {
			//write something for comment
		}
	});
};