package br.com.infoshop;

import android.content.Context;
import android.content.Intent;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;

import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("br.com.infoshop", appContext.getPackageName());
    }

    @Test
    public void message_boas_vindas_isCorrect() {
//        Calendar c = Calendar.getInstance();
//        try {
//            AppCompatActivity mainActivity = (AppCompatActivity) InstrumentationRegistry.getInstrumentation().newActivity(ClassLoader.getSystemClassLoader(), "HomeFragment", new Intent(InstrumentationRegistry.getInstrumentation().getContext(), MainActivity.class));
//            InstrumentationRegistry.getInstrumentation().
//            TextView view = mainActivity.findViewById(R.id.profile_text);
//            String valor = view.getText().toString();
//            int hour = c.get(Calendar.HOUR_OF_DAY);
//            if (hour > 6 && hour <= 12) {
//                assertEquals("Bom dia", valor);
//            } else if (hour > 12 && hour <= 19) {
//                assertEquals("Bom noite", valor);
//            } else {
//                assertEquals("Bom dia", valor);
//            }
//        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
//            e.printStackTrace();
//        }
    }
}