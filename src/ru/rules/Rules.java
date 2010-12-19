/*
 * Copyright (C) 2010 Denis Smolyar
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 */

package ru.rules;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Rules extends Activity
{
  private List <Data> data;
  private Integer answers = 0;
  private Iterator itr;

  private Button.OnClickListener onClickNext = new Button.OnClickListener () {
    public void onClick (View w) { nextQuestion (); }
  };

  private Button.OnClickListener onClickCorrectNext = new Button.OnClickListener () {
    public void onClick (View w) { correctAnswer () ; nextQuestion (); }
  };



  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);

    ImageView image = (ImageView) findViewById (R.id.image);
//    image.setImageResource (R.drawable.icon);

    setContentView (R.layout.main);
    start ();
  }



  private void start () {
    try {
      parseData ();
    } catch (IOException e) {
      AlertDialog.Builder dialog = new AlertDialog.Builder(this);
      dialog.setTitle("Exception Occured");
      dialog.setMessage(e.getMessage());
      dialog.setNeutralButton("Cool", null);
      dialog.create().show();

      Log.d (this.toString (), e.getMessage ());
      e.printStackTrace ();
    } catch (JSONException e) {
      AlertDialog.Builder dialog = new AlertDialog.Builder(this);
      dialog.setTitle("Exception Occured");
      dialog.setMessage(e.getMessage());
      dialog.setNeutralButton("Cool", null);
      dialog.create().show();

      Log.d (this.toString (), e.getMessage ());
      e.printStackTrace ();
    }

    answers = 0;
    nextQuestion ();
  }



  private void parseData () throws IOException, JSONException {
    InputStream is = this.getResources().openRawResource (R.raw.data);

    byte [] buffer = new byte [is.available ()];
    while (is.read(buffer) != -1);
    String jsontext = new String (buffer);
    JSONArray entries = new JSONArray (jsontext);

    data = new ArrayList<Data> ();

    for (int i=0; i<entries.length(); i++) {
      JSONObject post = entries.getJSONObject(i);
      Data d = new Data ();

      d.image = post.getString ("image");
      d.text1 = post.getString ("text1");
      d.text2 = post.getString ("text2");
      d.text3 = post.getString ("text3");
      d.text4 = post.getString ("text4");
      d.question = post.getString ("question");
      d.correctAnswer = post.getInt ("correctAnswer");

      data.add (d);
    }

    itr = data.iterator ();
  }


  private void nextQuestion () {
    if (!itr.hasNext ()) { showResult ();  return; }

    Data d = (Data) itr.next ();
    Button btn1 = (Button) findViewById (R.id.button1);
    Button btn2 = (Button) findViewById (R.id.button2);
    Button btn3 = (Button) findViewById (R.id.button3);
    Button btn4 = (Button) findViewById (R.id.button4);
    TextView tv = (TextView) findViewById (R.id.text);

    btn1.setText (d.text1); btn1.setOnClickListener (onClickNext);
    btn2.setText (d.text2); btn2.setOnClickListener (onClickNext);
    btn3.setText (d.text3); btn3.setOnClickListener (onClickNext);
    btn4.setText (d.text4); btn4.setOnClickListener (onClickNext);
    tv.setText (d.question);

    switch (d.correctAnswer) {
      case 1 : btn1.setOnClickListener (onClickCorrectNext); break;
      case 2 : btn2.setOnClickListener (onClickCorrectNext); break;
      case 3 : btn3.setOnClickListener (onClickCorrectNext); break;
      case 4 : btn4.setOnClickListener (onClickCorrectNext); break;
    }
  }



  private void correctAnswer () {
    answers += 1;
  }



  private void showResult () {
    AlertDialog.Builder dialog = new AlertDialog.Builder(this);
    DialogInterface.OnClickListener onClick =
            new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int id) {
                start ();
              }
            };

    dialog.setTitle("Конец");
    dialog.setMessage ("Правильных ответов: " + answers.toString ());
    dialog.setNeutralButton("Пройти еще раз", onClick);
    dialog.create().show();
  }
}
