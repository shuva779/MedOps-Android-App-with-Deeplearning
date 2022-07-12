package com.istiaksaif.medops;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetFileDescriptor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.istiaksaif.medops.ml.MedOps;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private EditText inputEditText;
    private TextView output;
    Interpreter model;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn= findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    EditText inputEditText;

                    inputEditText = findViewById(R.id.editTextNumberDecimal);
                    Float data= Float.parseFloat(inputEditText.getText().toString());
                    ByteBuffer byteBuffer= ByteBuffer.allocateDirect(1*4);
                    byteBuffer.putFloat(data);
                    byteBuffer.rewind();
                    System.out.println("#############################-");
                    System.out.println(data+"\n"+Arrays.toString(byteBuffer.array())
                            + "\nPosition: " + byteBuffer.position()
                            + "\nLimit: " + byteBuffer.limit());


                    MedOps model = MedOps.newInstance(getApplicationContext());

                    // Creates inputs for reference.
                    TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 1}, DataType.FLOAT32);
                    inputFeature0.loadBuffer(byteBuffer);

                    // Runs model inference and gets result.
                    MedOps.Outputs outputs = model.process(inputFeature0);
                    TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

                    // Releases model resources if no longer used.
                    TextView tv= findViewById(R.id.textView);
                    float[] data1=outputFeature0.getFloatArray();

                    tv.setText(outputFeature0.getDataType().toString());
                    tv.setText(String.valueOf(data1[0]));


                    model.close();

                }
                catch (Exception e)
                {
                    Toast.makeText(getApplicationContext(),"Issue...",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}


//inputEditText = findViewById(R.id.editTextNumberDecimal);
//        output = findViewById(R.id.textView);
//        Button btn= findViewById(R.id.button);
//
//        try {
//        model = new Interpreter(loadModelFile());
//        }catch (Exception e){
//        e.printStackTrace();
//        }
//        btn.setOnClickListener(new View.OnClickListener() {
//@Override
//public void onClick(View view) {
//        try{
//        float predict = inference(inputEditText.getText().toString());
//        output.setText(Float.toString(predict));
//        }
//        catch (Exception e)
//        {
//        Toast.makeText(getApplicationContext(),"Issue...",Toast.LENGTH_LONG).show();
//        }
//        }
//        });
//        }
//public float inference(String s){
//        Float [] input = new Float[1];
//        input[0]=Float.valueOf(s);
//
//        Float[][] outputvalue = new Float[1][1];
//        model.run(outputvalue,input);
//        float inferedValue = outputvalue[0][0];
//        return inferedValue;
//        }
//
//private MappedByteBuffer loadModelFile() throws IOException{
//        AssetFileDescriptor fileDescriptor = this.getAssets().openFd("model1.tflite");
//        FileInputStream fileInputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
//        FileChannel fileChannel = fileInputStream.getChannel();
//        long startOffset = fileDescriptor.getStartOffset();
//        long declar = fileDescriptor.getDeclaredLength();
//        return  fileChannel.map(FileChannel.MapMode.READ_ONLY,startOffset,declar);
//        }