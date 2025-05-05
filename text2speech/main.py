from fastapi import FastAPI, File, UploadFile, HTTPException, BackgroundTasks
from fastapi.responses import FileResponse
from models.request_model import TextRequest
from services.tts_service import generate_tts_audio
from services.whisper_service import transcribe_audio_file

app = FastAPI()


@app.post("/tts/")
async def text_to_speech(request: TextRequest, background_tasks: BackgroundTasks):
    try:
        filepath = generate_tts_audio(request.text)
        background_tasks.add_task(filepath.unlink)  # Xoá file sau khi gửi
        return FileResponse(str(filepath), media_type="audio/mpeg", filename="output.mp3")
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@app.post("/transcribe/")
async def transcribe_audio(file: UploadFile = File(...)):
    try:
        return transcribe_audio_file(file)
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
