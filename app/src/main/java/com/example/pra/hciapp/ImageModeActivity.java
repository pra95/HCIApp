//
// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license.
//
// Microsoft Cognitive Services (formerly Project Oxford): https://www.microsoft.com/cognitive-services
//
// Microsoft Cognitive Services (formerly Project Oxford) GitHub:
// https://github.com/Microsoft/Cognitive-Emotion-Android
//
// Copyright (c) Microsoft Corporation
// All rights reserved.
//
// MIT License:
// Permission is hereby granted, free of charge, to any person obtaining
// a copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to
// permit persons to whom the Software is furnished to do so, subject to
// the following conditions:
//
// The above copyright notice and this permission notice shall be
// included in all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED ""AS IS"", WITHOUT WARRANTY OF ANY KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
// OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
//

package com.example.pra.hciapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.microsoft.projectoxford.emotion.EmotionServiceClient;
import com.microsoft.projectoxford.emotion.EmotionServiceRestClient;
import com.microsoft.projectoxford.emotion.contract.FaceRectangle;
import com.microsoft.projectoxford.emotion.contract.RecognizeResult;
import com.microsoft.projectoxford.emotion.rest.EmotionServiceException;
import com.microsoft.projectoxford.face.FaceServiceRestClient;
import com.microsoft.projectoxford.face.contract.Face;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ImageModeActivity extends AppCompatActivity {

    private Button captureBtn;
    private ImageView displayImg;
    //private TextView resT;

    private MainServerActivity serverAct;

    String mCurrentPhotoPath;
    static final int REQUEST_TAKE_PHOTO = 6;

    private static final String OUTSIDE_IMAGE_MODE = "$$get_outside_image_mode_$$";
    private static final String PREPARE_TO_RECEIVE_IMAGE = "$$alpha_to_bravo_image_is_comming$$";

    private Uri mImageUri;
    private Bitmap mBitmap;

    private float maxE = 0;
    private static volatile String Emo;

    private EmotionServiceClient client;

    File f = null;

    Uri photoURI;
    Uri mUriPhotoTaken;

    private TextView results;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_mode);

        results = (TextView) findViewById(R.id.output);

        if (client == null) {
            client = new EmotionServiceRestClient(getString(R.string.subscription_key));
        }
        
        //resT = (TextView) findViewById(R.id.resultView);
        captureBtn = (Button) findViewById(R.id.imageCaptureButton);
        displayImg = (ImageView) findViewById(R.id.imageDisplay);
        serverAct = new MainServerActivity();
    }

    public void pressedImageCapture(View view) {

        results.setText("");
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if(takePictureIntent.resolveActivity(getPackageManager()) != null) {

            File photoFile = null;

            try {

                photoFile = createImageFile();
            }
            catch (Exception e) {

                e.printStackTrace();
            }

            if(photoFile != null) {

                photoURI = FileProvider.getUriForFile(this, "com.example.android.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                mUriPhotoTaken = Uri.fromFile(photoFile);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createImageFile() throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {


            setPic();
            if(data == null || data.getData() == null)
                mImageUri = mUriPhotoTaken;
            else
                mImageUri = data.getData();
            
            mBitmap = ImageHelper.loadSizeLimitedBitmapFromUri(mImageUri, getContentResolver());
            
            if(mBitmap != null) {

                captureBtn.setVisibility(View.INVISIBLE);
                Log.d("debugString", "Was set invisible");
                //resT.setText("RESULTS: ");
                doRecognize();
                Log.d("debugString", "dorecognize over Was set visible");

            }
        }
        else {

            Log.d("debug", "some problem");
        }
        serverAct.sendMessage(results.getText().toString());
    }

    public void doRecognize() {

        // Do emotion detection using auto-detected faces.

        try {
            new doRequest(false).execute();
        } catch (Exception e) {
            Log.d("debugString", "Error during doRequest in doRecognize: " + e.getMessage());

        }


        String faceSubscriptionKey = getString(R.string.faceSubscription_key);
        if (faceSubscriptionKey.equalsIgnoreCase("Please_add_the_face_subscription_key_here")) {
            Log.d("debugString", "No subscription key");
        } else {
            // Do emotion detection using face rectangles provided by Face API.
            try {
                new doRequest(true).execute();
            } catch (Exception e) {
                
                Log.d("Debug", "Err " + e.getMessage());
            }
        }

    }


    private class doRequest extends AsyncTask<String, String, List<RecognizeResult>> {
        // Store error message
        private Exception e = null;
        private boolean useFaceRectangles = false;
        public MainServerActivity serverAt = new MainServerActivity();

        public doRequest(boolean useFaceRectangles) {

            this.useFaceRectangles = useFaceRectangles;
        }

        @Override
        protected List<RecognizeResult> doInBackground(String... args) {
            if (!this.useFaceRectangles) {
                try {
                    return processWithAutoFaceDetection();
                } catch (Exception e) {
                    this.e = e;    // Store error
                    Log.d("debugString" , "process without face detection error : " + e.getMessage());
                }
            } else {
                try {
                    return processWithFaceRectangles();
                } catch (Exception e) {
                    this.e = e;    // Store error
                    Log.d("debugString" , "process with face detection error : " + e.getMessage());
                }
            }


            return null;
        }

        @Override
        protected void onPostExecute(final List<RecognizeResult> result) {
            super.onPostExecute(result);
            // Display based on error existence

            if (!this.useFaceRectangles) {
                Log.d("debugString", "Recognizing emotions with auto-detected face rectangles...");
                
            } else {
                Log.d("debugString", "Recognizing emotions with existing face rectangles from Face API....");
                
            }
            if (e != null) {
                Log.d("debugString", "SOME ERROR: " + e.getMessage());
                
                this.e = null;
            } else {
                if (result.size() == 0) {
                    Log.d("debugString", "No emotions detected.");
                    //resT.append("\nNO EMOTIONS DETECTED.");
                    results.setText("No Emotions Detected.");
                    
                } else {
                    Integer count = 0;
                    // Covert bitmap to a mutable bitmap by copying it
                    Bitmap bitmapCopy = mBitmap.copy(Bitmap.Config.ARGB_8888, true);
                    Canvas faceCanvas = new Canvas(bitmapCopy);
                    faceCanvas.drawBitmap(mBitmap, 0, 0, null);
                    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(5);
                    paint.setColor(Color.RED);

                    for (RecognizeResult r : result) {
                        /*resT.append(String.format("\nFace #%1$d \n", count));
                        resT.append(String.format("\t anger: %1$.5f\n", r.scores.anger));
                        resT.append(String.format("\t contempt: %1$.5f\n", r.scores.contempt));
                        resT.append(String.format("\t disgust: %1$.5f\n", r.scores.disgust));
                        resT.append(String.format("\t fear: %1$.5f\n", r.scores.fear));
                        resT.append(String.format("\t happiness: %1$.5f\n", r.scores.happiness));
                        resT.append(String.format("\t neutral: %1$.5f\n", r.scores.neutral));
                        resT.append(String.format("\t sadness: %1$.5f\n", r.scores.sadness));
                        resT.append(String.format("\t surprise: %1$.5f\n", r.scores.surprise));
                        resT.append(String.format("\t face rectangle: %d, %d, %d, %d", r.faceRectangle.left, r.faceRectangle.top, r.faceRectangle.width, r.faceRectangle.height));


                        resT.append("\nFace count: " + count);
                        resT.append("\nanger: " + (float)(r.scores.anger));
                        resT.append("\ncontempt: " + (float)r.scores.contempt);
                        resT.append("\nfear: " + (float)r.scores.fear);
                        resT.append("\ndisgust: " + (float)r.scores.disgust);
                        resT.append("\nhappiness: " + (float)r.scores.happiness);
                        resT.append("\nneutral: " + (float)r.scores.neutral);
                        resT.append("\nsadness: " + (float)r.scores.sadness);
                        resT.append("\nsurprise: " + (float)r.scores.surprise);
                        Log.d("debugString", "Anger" + (float)r.scores.anger);
*/

                        if((float)(r.scores.anger) > maxE) {

                            maxE = (float)(r.scores.anger);
                            Emo = "Angry";

                        }
                        if((float)(r.scores.fear) > maxE) {

                            maxE = (float)(r.scores.fear);
                            Emo = "Scared";
                        }
                        if((float)(r.scores.happiness) > maxE) {

                            maxE = (float)(r.scores.happiness);
                            Emo = "Happy";
                        }
                        if((float)(r.scores.neutral) > maxE) {

                            maxE = (float)(r.scores.neutral);
                            Emo = "Neutral";
                        }
                        if((float)(r.scores.sadness) > maxE) {

                            maxE = (float)(r.scores.sadness);
                            Emo = "Sad";
                        }
                        if((float)(r.scores.surprise) > maxE) {

                            maxE = (float)(r.scores.surprise);
                            Emo = "Surprised";
                        }

                        results.setText("You look " + Emo + ".");
                        serverAct.sendMessage(Emo);


                        Log.d("debugString", "THIS IS WHAT WE ARE GETTING:" + String.valueOf(r));
                        faceCanvas.drawRect(r.faceRectangle.left, r.faceRectangle.top, r.faceRectangle.left + r.faceRectangle.width, r.faceRectangle.top + r.faceRectangle.height, paint);
                        count++;
                    }
                }
            }
        }
    }

    private List<RecognizeResult> processWithAutoFaceDetection() throws EmotionServiceException, IOException {
        Log.d("emotion", "Start emotion detection with auto-face detection");

        Gson gson = new Gson();

        // Put the image into an input stream for detection.
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());

        long startTime = System.currentTimeMillis();
        // -----------------------------------------------------------------------
        // KEY SAMPLE CODE STARTS HERE
        // -----------------------------------------------------------------------

        List<RecognizeResult> result = null;
        //
        // Detect emotion by auto-detecting faces in the image.
        //
        result = this.client.recognizeImage(inputStream);

        String json = gson.toJson(result);
        Log.d("result", json);

        Log.d("emotion", String.format("Detection done. Elapsed time: %d ms", (System.currentTimeMillis() - startTime)));
        // -----------------------------------------------------------------------
        // KEY SAMPLE CODE ENDS HERE
        // -----------------------------------------------------------------------
        return result;
    }

    private List<RecognizeResult> processWithFaceRectangles() throws EmotionServiceException, com.microsoft.projectoxford.face.rest.ClientException, IOException {
        Log.d("emotion", "Do emotion detection with known face rectangles");
        Gson gson = new Gson();

        // Put the image into an input stream for detection.
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());

        long timeMark = System.currentTimeMillis();
        Log.d("emotion", "Start face detection using Face API");
        FaceRectangle[] faceRectangles = null;
        String faceSubscriptionKey = getString(R.string.faceSubscription_key);
        FaceServiceRestClient faceClient = new FaceServiceRestClient(faceSubscriptionKey);
        Face faces[] = faceClient.detect(inputStream, false, false, null);
        Log.d("emotion", String.format("Face detection is done. Elapsed time: %d ms", (System.currentTimeMillis() - timeMark)));

        if (faces != null) {
            faceRectangles = new FaceRectangle[faces.length];

            for (int i = 0; i < faceRectangles.length; i++) {
                // Face API and Emotion API have different FaceRectangle definition. Do the conversion.
                com.microsoft.projectoxford.face.contract.FaceRectangle rect = faces[i].faceRectangle;
                faceRectangles[i] = new com.microsoft.projectoxford.emotion.contract.FaceRectangle(rect.left, rect.top, rect.width, rect.height);
            }
        }

        List<RecognizeResult> result = null;
        if (faceRectangles != null) {
            inputStream.reset();

            timeMark = System.currentTimeMillis();
            Log.d("emotion", "Start emotion detection using Emotion API");
            // -----------------------------------------------------------------------
            // KEY SAMPLE CODE STARTS HERE
            // -----------------------------------------------------------------------
            result = this.client.recognizeImage(inputStream, faceRectangles);

            String json = gson.toJson(result);
            Log.d("result", json);
            // -----------------------------------------------------------------------
            // KEY SAMPLE CODE ENDS HERE
            // -----------------------------------------------------------------------
            Log.d("emotion", String.format("Emotion detection is done. Elapsed time: %d ms", (System.currentTimeMillis() - timeMark)));
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                captureBtn.setVisibility(View.VISIBLE);
                //serverAct.sendMessage(Emo);
            }
        });
        return result;
    }


    private void setPic() {

        // Get the dimensions of the View
        int targetW = displayImg.getWidth();
        int targetH = displayImg.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        displayImg.setImageBitmap(bitmap);


    }

    private Uri galleryAddPic() {

        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);

        return contentUri;
    }


    private void sendImageToServer() {

        serverAct.sendMessage(PREPARE_TO_RECEIVE_IMAGE);


        f = new File(mCurrentPhotoPath);
        byte[] bytes = new byte[(int) f.length()];
        BufferedInputStream bis;

        try {

            bis = new BufferedInputStream(new FileInputStream(f));
            bis.read(bytes, 0, bytes.length);
            bis.close();

            ObjectOutputStream oos = new ObjectOutputStream(MainServerActivity.socket.getOutputStream());
            oos.writeObject(bytes);
            oos.flush();
            //oos.close();
            successfullySent();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void successfullySent() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ImageModeActivity.this, "Image sent", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void sendEmotion(String ress) {

        serverAct.sendMessage(ress);
        Log.d("debugString", "Message sent fron image as : " + ress);
    }

    @Override
    public void onBackPressed() {

        serverAct.sendMessage(OUTSIDE_IMAGE_MODE);

        super.onBackPressed();
    }
}
