import pyttsx3
import uuid
from pathlib import Path
import time


def generate_tts_audio(text: str) -> Path:
    if not text.strip():
        raise ValueError("Văn bản không được để trống")

    filepath = Path("temp_files") / f"tts_{uuid.uuid4().hex}.mp3"
    filepath.parent.mkdir(exist_ok=True)

    engine = pyttsx3.init()
    voices = engine.getProperty('voices')
    engine.setProperty('rate', 140)
    engine.setProperty('voice', voices[1].id)

    engine.save_to_file(text.strip(), str(filepath))
    engine.runAndWait()

    timeout = 20
    while not filepath.exists() and timeout > 0:
        time.sleep(0.1)
        timeout -= 0.1

    if not filepath.exists():
        raise RuntimeError("Không thể tạo file âm thanh")

    return filepath
