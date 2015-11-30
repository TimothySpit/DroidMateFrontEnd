$.getScript('//cdnjs.cloudflare.com/ajax/libs/flot/0.8.2/jquery.flot.min.js',function(){
  $.getScript('//cdnjs.cloudflare.com/ajax/libs/flot/0.8.2/jquery.flot.pie.min.js',function(){
    $.getScript('//cdnjs.cloudflare.com/ajax/libs/flot/0.8.2/jquery.flot.resize.min.js',function(){
      
      
      // static charts
      var d1 = [];
      for (var i = 0; i < 14; i += 0.2) {
         d1.push([i, Math.sin(i)]);
      } 
      var d2 = [[0, 3], [4, 8], [8, 5], [9, 13]];
      var d3 = [[0, 12], [7, 12], [12, 13]];
        
      // line
      $.plot("#chart2",[d1],{yaxis:{show:false},grid:{borderColor:'#ccc'}});
      $.plot("#chart3",[d2,d3],{yaxis:{show:false},grid:{borderColor:'#ccc'},series:{color:'#ff4444',lines:{show:true},points:{show:true}}});
      $.plot("#chart4",[d3],{yaxis:{show:false},grid:{borderColor:'#ccc'},series:{color:'#4444ff'}});
      
      // pie
      $.plot("#chart5",[{data:70,color:'#5566ff'},{data:20,color:'#ddd'}],{series:{pie:{show: true,innerRadius: 0.6}}});
      $.plot("#chart6",[{data:10,color:'#5566ff'},{data:30,color:'#ddd'}],{series:{pie:{show: true,innerRadius: 0.6}}});
    
      // real-time chart
      // we use an inline data source in the example, usually data would
      // be fetched from a server
      var data = [], totalPoints = 200;
      function getRandomData() {
        
        if (data.length > 0)
          data = data.slice(1);
        
        // do a random walk
        while (data.length < totalPoints) {
          var prev = data.length > 0 ? data[data.length - 1] : 50;
          var y = prev + Math.random() * 10 - 5;
          if (y < 0)
            y = 0;
          if (y > 100)
            y = 100;
          data.push(y);
        }
      
        // zip the generated y values with the x values
        var res = [];
        for (var i = 0; i < data.length; ++i)
          res.push([i, data[i]])
          return res;
      }
      
      // setup control widget
      var updateInterval = 500;
      $("#updateInterval").val(updateInterval).change(function () {
      var v = $(this).val();
      if (v && !isNaN(+v)) {
        updateInterval = +v;
        if (updateInterval < 1)
            updateInterval = 1;
            if (updateInterval > 2000)
             updateInterval = 2000;
             $(this).val("" + updateInterval);
            }
      });
      
      // setup plots
      var options = {
        grid:{borderColor:'#ccc'},
        series:{shadowSize:0,color:"#33ff33"},
        yaxis:{min:0,max:100},
        xaxis:{show:true}
      };
       
      var plot = $.plot($("#chart1"), [ getRandomData() ], options);
          
      function update() {
        plot.setData([ getRandomData() ]);
        plot.draw();
        setTimeout(update, updateInterval);
      }
      
      update();
      
    });// end getScript (resize)
  });// end getScript (pie)
});// end getScript
