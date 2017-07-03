$(document).ready(function() {
	Highcharts.chart('chartContainer', {
		chart: {
			type: 'areaspline'
		},
		title: {
			text: 'Points distribution'
		},
		legend: {
			layout: 'vertical',
			align: 'left',
			verticalAlign: 'top',
			x: 150,
			y: 100,
			floating: true,
			borderWidth: 1,
			backgroundColor: (Highcharts.theme && Highcharts.theme.legendBackgroundColor) || '#FFFFFF'
		},
		xAxis: {
			title: {
				text: 'Points'
			}
		},
		yAxis: {
			title: {
				text: 'Probability'
			}
		},
		tooltip: {
			shared: true,
			valueSuffix: '%',
			formatter: function(){
				var s = '<b>' + this.x + '</b>';
				$.each(this.points, function () {
					s += '<br/>' + '<span style="color:' + this.series.color + '">' + this.series.name + '</span>' + ': ' + (this.y*100).toFixed(3) + '%';
				});

				return s;
			}
		},
		credits: {
			enabled: false
		},
		plotOptions: {
			areaspline: {
				fillOpacity: 0.5
			}
		},
		series: [{
			name: 'Medium-level vs Random',
			data: [[48272,0.00009], [48402,0.00125], [48558, 0.00384], [48713, 0.00125], [48843,0.00009]]
		}, {
			name: 'High-level vs Random',
			data: [[32387,0.00008], [32531,0.00113], [32704, 0.00347], [32876, 0.00113], [33020,0.00008]]
		}, {
			name: 'Medium-level vs Legacy',
			data: [[6196,0.00008], [6250,0.00101], [6358, 0.00743], [6465, 0.00101], [6519,0.00008]]
		}, {
			name: 'High-level vs Legacy',
			data: [[5812,0.00008], [5865,0.00101], [5972, 0.00750], [6078, 0.00101], [6131,0.00008]]
		}]
	});
});