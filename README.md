this line

StringBuffer text = new StringBuffer(ResourceUtil.loadText(url));
should point to a new jar which includes also our tips
jar:file:/home/tomerb/dev/idea-IC-141.713.2/lib/resources_en.jar!/tips/SmartTypeAfterNew.html

example text
<html>
<head>
    <link rel="stylesheet" type="text/css" href="css/tips.css">
</head>
<body>


    <p>The SmartType code completion may be used after the
      <span class="code_keyword">new</span> keyword,
      to instantiate an object of the expected type. For example, type</p>
      <p class="image"><img src="images/smart_type_after_new_1.png"></p>
      <p>and press <span class="shortcut">&shortcut:SmartTypeCompletion;</span>:</p>
      <p class="image"><img src="images/smart_type_after_new_2.png"></p>



</body>
</html>


~/dev/idea-source/resources-en/src/tips
com.intellij.ide.util.TipPanel
com.intellij.ide.actions.ShowTipsAction