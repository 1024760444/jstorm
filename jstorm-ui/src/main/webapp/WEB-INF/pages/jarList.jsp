<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ct" uri="http://jstorm.alibaba.com/jsp/tags" %>
<%--
  Licensed to the Apache Software Foundation (ASF) under one or more
  contributor license agreements.  See the NOTICE file distributed with
  this work for additional information regarding copyright ownership.
  The ASF licenses this file to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
  --%>

<html>
<head>
    <jsp:include page="layout/_head.jsp"/>
</head>
<body>
<jsp:include page="layout/_header.jsp"/>

<div class="container-fluid">
    <!-- ========================================================== -->
    <!------------------------- topology summary --------------------->
    <!-- ========================================================== -->
    <h2>Task Jars Summary</h2>
    <a href="#" class="btn btn-sm btn-primary">Add Jar</a>
    <table class="table table-bordered table-hover table-striped sortable center" >
        <thead>
        <tr>
            <th>Task Name</th>
            <th>Path</th>
            <th>Jar Name</th>
            <th>Main Class</th>
            <th>Main Args</th>
            <th>Upload Time</th>
            <th>Behavior</th>
        </tr>
        </thead>
        <tbody>
            <tr>
                <td>apmhub1.3</td>
                <td>${path}</td>
                <td>original-apmhub1.3.jar</td>
                <td>apm.shield.hub.bin.ApmTopologyCluster</td>
                <td>${name}</td>
                <td>2016-08-08 17:49:45</td>
                <td><a href="#" class="btn btn-primary btn-xs" >submit</a></td>
            </tr>
        </tbody>
    </table>
</div>

<jsp:include page="layout/_footer.jsp"/>
<script src="assets/js/echarts/echarts.js"></script>
<script src="assets/js/storm.js"></script>
<script>
    $(function () {
        $('[data-toggle="tooltip"]').tooltip();

        //draw metrics charts
        $.getJSON("api/v2/cluster/${clusterName}/metrics", function (data) {
            var echarts = new EChart();
            data = data['metrics'];
            var width = ($('.container-fluid').width() / data.length) - 2;
            data.forEach(function (e) {
                var selector = document.getElementById('chart-' + e.name);
                selector.setAttribute("style", "width:"+ width + "; height: 100px");
                echarts.init(selector, e);
            });

            $("#chart-tr").toggleClass("hidden");
        });
    });
</script>
</body>
</html>