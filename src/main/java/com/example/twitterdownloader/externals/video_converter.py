import cv2 as cv
import numpy as np
import imageio.v3 as iio
import imageio
from pygifsicle import optimize
import typing
import sys

'''
GIF Settings 

Size:
Original(up to 800px)
Original(up to 600px)
800 X 600
800 X 480
640 X 400
800 X AUTO
600 X AUTO
540 X AUTO
480 X AUTO
400 X AUTO
320 X AUTO

FPS:
5,10,15,20,25,30, original(less than 30 : -1)

duration:
from: ?sec
to: ?sec
'''

imageio.plugins.freeimage.download()
print("opencv version", cv.__version__)

class VideoConverter():
    def __init__(self, video: str, tar_width: typing.Union[int, float, None], tar_height: typing.Union[int, float, None], flag, 
                 fps: int, duration: tuple[int], converted_filepath: str) -> None:
        self.covnerted_filepath = converted_filepath
        self.output = self.covnerted_filepath + "/" + self.__makeFilename(self.__extractFilename(video))
        print(self.output)
        self.video = cv.VideoCapture()
        self.__openVideo(video)
        self.width = int(tar_width)
        self.height = int(tar_height)
        # flag: 0: original(800) 1: original(600) 2: others
        self.flag = flag
        self.fps = fps
        self.duration = duration
        self.covnerted = None
        
        
    def __extractFilename(self, filePath: str) -> str:
        return filePath.split('/')[-1]
    
    def __makeFilename(self, filename: str) -> str:
        return filename.split('.')[0] + '.gif'
    
    def __openVideo(self, video_uri: str) -> bool:
        try:
            self.video.open(video_uri)
        except IOError:
            print("Failed to open file!")
            return False
        return True
        
    def __resize(self) -> None:
        
        origin_width = self.video.get(cv.CAP_PROP_FRAME_WIDTH)
        origin_height = self.video.get(cv.CAP_PROP_FRAME_HEIGHT)
        origin_fps = int(self.video.get(cv.CAP_PROP_FPS))
        ratio = origin_height / origin_width
        if(self.fps == -1):
            if(origin_fps > 30):
                self.fps = 30
            else:
                self.fps = origin_fps
                
        if(self.flag == 0):
            if(origin_width > 800):
                self.width = 800
                self.height = int(self.width * ratio)
                
        if(self.flag == 1):
            if(origin_width > 600):
                self.width = 600
                self.height = int(self.width * ratio)
        
        if(self.height == -1):
            self.height = int(self.width * ratio)
        
        return 
    
    def __validate(self) -> bool:
        start = self.duration[0]
        end = self.duration[1]
        if(start >= end):
            print("Invalid interval")
            return False
        if(start - end > 30):
            print("Too long")
            return False
        return True
    
    def convert(self) -> any:
        if(self.__validate == False):
            ValueError("Invalid start & end time, your start time may not exceed end time, the length of gif should not exceed 20s in total")
        
        self.__resize()
        
        retval = True
        frame = 0
        start = int(self.duration[0])
        end = int(self.duration[1])
        origin_fps = int(self.video.get(cv.CAP_PROP_FPS))
        start_frame = 0
        end_frame = 0
        if(start == end == 0):
            start_frame = 0
            end_frame = self.video.get(cv.CAP_PROP_FRAME_COUNT)
            if(end_frame / origin_fps > 30.0):
                ValueError("Video length longer than 30s")
        else:
            start_frame = origin_fps * start
            end_frame = origin_fps * end
        img_seq = []
        while self.video.grab():
            pos_frame = self.video.get(cv.CAP_PROP_POS_FRAMES)
            if((pos_frame >= start_frame) and (pos_frame <= end_frame)):
                # print("dealing with:", pos_frame)
                retval, frame = self.video.retrieve(None)
                if(retval == False):
                    break
                frame = cv.cvtColor(frame, cv.COLOR_BGR2RGB)
                # print(self.width, self.height)
                frame = cv.resize(frame, (self.width, self.height))
                img_seq.append(frame)

        self.video.release()
        frames = np.stack(img_seq, axis=0)
        imageio.v3.imwrite(self.output, frames, fps=self.fps)
        return True
    
if __name__ == '__main__':
    retval = VideoConverter(video=sys.argv[1], tar_width=eval(sys.argv[2]), tar_height=eval(sys.argv[3]), flag=int(sys.argv[4]), fps=int(sys.argv[5]), duration=tuple(sys.argv[6].split(',')), converted_filepath=sys.argv[7]).convert()