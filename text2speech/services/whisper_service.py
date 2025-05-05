from faster_whisper import WhisperModel, BatchedInferencePipeline
import shutil
import uuid
import os
from fastapi import UploadFile
from mutagen.mp3 import MP3
from mutagen.id3 import USLT

# Load model 1 lần duy nhất
model = WhisperModel("small", device="cuda", compute_type="float16")
pipeline = BatchedInferencePipeline(model=model)

def get_lyrics_from_mp3(file_path: str) -> str:
    try:
        audio = MP3(file_path)
        for tag in audio.tags.values():
            if isinstance(tag, USLT):
                return tag.text
    except:
        pass
    return ""

def transcribe_audio_file(file: UploadFile):
    ext = file.filename.split('.')[-1]
    base = f"temp_{uuid.uuid4().hex}"
    input_path = os.path.abspath(f"temp_files/{base}.{ext}")
    os.makedirs("temp_files", exist_ok=True)

    try:
        with open(input_path, "wb") as f:
            shutil.copyfileobj(file.file, f)

        # Nếu có lyrics trong metadata, trả luôn
        if ext.lower() == "mp3":
            lyrics = get_lyrics_from_mp3(input_path)
            if lyrics.strip():
                return {"text": lyrics.strip(), "source": "metadata"}

        # Sử dụng pipeline để tăng tốc
        segments, _ = pipeline.transcribe(input_path, language="vi", beam_size=5)
        text = " ".join(segment.text for segment in segments)

        return {"text": text, "source": "faster-whisper"}

    finally:
        if os.path.exists(input_path):
            os.remove(input_path)
