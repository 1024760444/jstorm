<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
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
    <button id="btn_add" class="btn btn-sm btn-primary"  style="margin-bottom: 3px;" >Add</button>
    <table class="table table-bordered table-hover table-striped sortable center" >
        <thead>
        <tr>
            <th>Jar Name</th>
            <th>Full Path</th>
            <th>Args</th>
            <th>Behavior</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="jarInfo" items="${jarFileArray}" varStatus="index">
            <tr>
                <td>${jarInfo.jarName}</td>
                <td>${jarInfo.fullPath}</td>
                <td>${jarInfo.args}</td>
                <td><button id="submit_topo" class="btn btn-primary btn-xs" >Submit</button></td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>

	<div class="modal fade" id="submitTopology" tabindex="-1" role="dialog"
		aria-labelledby="submitTopologyLabel">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title" id="myModalLabel">Submit Topology</h4>
				</div>
				<div class="modal-body">
					<form role="form" action="submitTopology?name=${clusterName}" method="post" >
						<div class="form-group">
							<label for="name">Jar Name : </label> <input type="text"
								class="form-control" readonly="readonly" id="submitTopoJar" name="submitTopoJar" placeholder="Jar Name">
						</div>
						<div class="form-group">
							<label for="name">Full Path : </label> <input type="text"
								class="form-control" readonly="readonly" id="submitTopoPath" name="submitTopoPath" placeholder="Main Class">
						</div>
						<div class="form-group">
							<label for="name">Args : </label> <input type="text"
								class="form-control" id="submitTopoArgs" name="submitTopoArgs" placeholder="Main Args">
						</div>
						<div class="form-group">
							<label for="name">PassWord : </label> <input type="text"
								class="form-control" id="passWord" name="passWord" placeholder="PassWord">
						</div>
						<button type="submit" class="btn btn-default"> Submit </button>
					</form>
				</div>
			</div>
		</div>
	</div>


	<div class="modal fade" id="submitJarFile" tabindex="-1" role="dialog"
		aria-labelledby="submitJarFileLabel">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title" id="myModalLabel">New</h4>
				</div>
				<div class="modal-body">
					<form role="form" action="uploadJar?name=${clusterName}" method="post" enctype="multipart/form-data">
						<div class="form-group">
							<label for="name">Jar Name : </label> <input type="text"
								class="form-control" id="jarName" name="jarName" placeholder="Jar Name">
						</div>
						<div class="form-group">
							<label for="name">Main Class : </label> <input type="text"
								class="form-control" id="mainClass" name="mainClass" placeholder="Main Class">
						</div>
						<div class="form-group">
							<label for="name">Main Args : </label> <input type="text"
								class="form-control" id="mainArgs" name="mainArgs" placeholder="Main Args">
						</div>
						<div class="form-group">
							<label for="name">Keyword : </label> <input type="text"
								class="form-control" id="keyword" name="keyword" placeholder="Keyword">
						</div>
						<div class="form-group">
							<label for="inputfile">Input Jar File</label> <input type="file"
								id="inputfile"  name="inputfile">
							<p class="help-block">Jstrom Jar File Local Path</p>
						</div>
						<button type="submit" class="btn btn-default"> Add </button>
					</form>
				</div>
			</div>
		</div>
	</div>

	<jsp:include page="layout/_footer.jsp"/>
<script src="assets/js/echarts/echarts.js"></script>
<script src="assets/js/storm.js"></script>
<script>
	$("#btn_add").click(function() {
		$("#submitJarFileLabel").text("Add");
		$('#submitJarFile').modal();
	});
	
	$("#submit_topo").click(function() {
		
		var tr = $(this).parent().parent();
		var td0 = tr.children("td:eq(0)");
		var td1 = tr.children("td:eq(1)");
		var td2 = tr.children("td:eq(2)");
		
		$("#submitTopoJar").val(td0.text());
		$("#submitTopoPath").val(td1.text());
		$("#submitTopoArgs").val(td2.text());
		
		$("#submitTopologyLabel").text("Submit");
		$('#submitTopology').modal();
	});
	
	$(function() {
		$('[data-toggle="tooltip"]').tooltip();

		//draw metrics charts
		$.getJSON("api/v2/cluster/${clusterName}/metrics", function(data) {
			var echarts = new EChart();
			data = data['metrics'];
			var width = ($('.container-fluid').width() / data.length) - 2;
			data.forEach(function(e) {
				var selector = document.getElementById('chart-' + e.name);
				selector.setAttribute("style", "width:" + width
						+ "; height: 100px");
				echarts.init(selector, e);
			});

			$("#chart-tr").toggleClass("hidden");
		});
	});
</script>
</body>
</html>