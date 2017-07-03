/*jQuery(function () {
	$.get('https://raw.githubusercontent.com/hecoding/Pac-Man/master/grammar/high_level.bnf', function(data) {
		$("#high-level-grammar").text(data);
	}, 'text');
});*/

$(document).ready(function() {
	$.get('https://raw.githubusercontent.com/hecoding/Pac-Man/master/grammar/low_level.bnf', function(data) {
		$("#low-level-grammar-text").text(data);
	}, 'text');

	$.get('https://raw.githubusercontent.com/hecoding/Pac-Man/master/grammar/medium_level.bnf', function(data) {
		$("#medium-level-grammar-text").text(data);
	}, 'text');

	$.get('https://raw.githubusercontent.com/hecoding/Pac-Man/master/grammar/high_level.bnf', function(data) {
		$("#high-level-grammar-text").text(data);
	}, 'text');

	$('.btn-2states').click(function(e) {
		e.preventDefault();
		$(this).toggleClass('active');
	});
});

/*window.onload = function(){
	$.get('https://raw.githubusercontent.com/hecoding/Pac-Man/master/grammar/high_level.bnf', function(data) {
		$("#high-level-grammar").text(data);
	}, 'text');
};*/