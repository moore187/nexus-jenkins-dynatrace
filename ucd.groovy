import groovy.json.JsonBuilder
import groovy.json.JsonSlurper

def environment = "TESTING2"// = p:component.environment
def url = "https://yey08400.live.dynatrace.com/api/v1/events/"// = p:url

def post = new URL("${url}")

HttpURLConnection conn = (HttpURLConnection) post.openConnection()

def jsonText = new File('file').getText()
def slurper = new JsonSlurper().parseText(jsonText)
def json = new JsonBuilder(slurper)
json.content.customProperties.putAt("Environment", "${environment}")
println(json.toPrettyString())

conn.setDoOutput(true)
conn.setRequestMethod("GET")
conn.setRequestProperty("Authorization", "API-Token zAZMjosmTKmk5YUQr1ObC")
conn.setRequestProperty("Content-Type", "application/json")
conn.getOutputStream().write(json.toPrettyString().getBytes())

println(conn.getInputStream().getText())
