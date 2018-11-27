Append this to the bottom of the generated plugin_dependencies.svg. This will create the clickable links.

  <!-- BEGIN MANUAL JAVASCRIPT -->
	<script type="text/javascript">
	//<![CDATA[
		var gTags = document.getElementsByTagName("g");
		for (var i = 0; i < gTags.length; i++) {
			var tspans = gTags[i].querySelectorAll("text tspan");
			if (tspans.length == 2) {
				var projectName = tspans[0].innerHTML;
				var jenkinsName = tspans[1].innerHTML;
				
				if (projectName === "ProjectName") {
					continue;
				}
				
				tspans[0].innerHTML = "<a target=\"_blank\" href=\"https://github.com/KernelHaven/" + projectName + "/\">" + projectName + "</a>";
				tspans[1].innerHTML = "<a target=\"_blank\" href=\"https://jenkins.sse.uni-hildesheim.de/view/KernelHaven/job/" + jenkinsName + "/\">" + jenkinsName + "</a>";
			}
		}
	//]]>
	</script>
  <!-- END MANUAL JAVASCRIPT -->
