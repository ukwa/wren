import storm
import random
import os
from subprocess import Popen, PIPE

class WebRenderBolt(storm.BasicBolt):
    
    def get_har_with_image(self, url, selectors=None):
        """Gets the raw HAR output from PhantomJs with rendered image(s)."""
        tmp = "%s/%s.json" % ("/var/tmp", str(random.randint(0, 1000000)))
        command = ["phantomjs", "netsniff-rasterize.js", url, tmp]
        if selectors is not None:
            command += selectors
        for item in command:
            storm.log("item: "+str(item))
        har = Popen(command, stdout=PIPE, stderr=PIPE)
        stdout, stderr = har.communicate()
        if not os.path.exists(tmp):
            storm.log("FAILED!")
            return "FAIL"
        with open(tmp, "r") as i:
            output = i.read()
        os.remove(tmp)
        storm.log("GOT "+output)
        return output

    
    def process(self, tup):
        url = tup.values[0]
        storm.log("HARing "+url)
        output = self.get_har_with_image(url)
        if output is not "FAIL":
            storm.emit(output, anchors=[tup])
            storm.ack(tup)

WebRenderBolt().run()