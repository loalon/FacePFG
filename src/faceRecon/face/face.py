class Face():
    def __init__(self, ulCorner, lrCorner, datetime):
        self.ulCorner = ulCorner
        self.lrCorner = lrCorner

        self.name=""
        self.datetime = datetime
        self.confidence = 0
        self.file=""
