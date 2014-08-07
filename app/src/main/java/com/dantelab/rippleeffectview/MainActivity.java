package com.dantelab.rippleeffectview;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dantelab.rippleeffectview.library.RippleEffectView;
import com.dantelab.rippleeffectview.library.RippleEffectViewContainer;
import com.dantelab.rippleeffectview.library.RippleUtils;


public class MainActivity extends Activity {

    private RippleEffectView rippleEffectView;
    private SeekBar mSeekBarAlpha, mSeekBarBlue, mSeekBarRed, mSeekBarGreen;
    private TextView mText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mText = (TextView) findViewById(R.id.text);
        rippleEffectView = (RippleEffectView) findViewById(R.id.ripple);

        ((CheckBox) findViewById(R.id.modeCheckBox)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                rippleEffectView.setReverse(isChecked);
            }
        });

        ((CheckBox) findViewById(R.id.clickCheckBox)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                rippleEffectView.setOnClickListener(isChecked ? new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mText.setText("Just ripple click");
                        Toast.makeText(MainActivity.this, "Just click", Toast.LENGTH_SHORT).show();
                        rippleEffectView.setBackgroundColor(Color.argb(64, (int)(Math.random()*255),(int)(Math.random()*255), (int)(Math.random()*255)));
                    }
                } : null);
            }
        });

        ((CheckBox) findViewById(R.id.longClickCheckBox)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                rippleEffectView.setOnLongClickListener(isChecked ? new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {

                        mText.setText("Just ripple long click");
                        rippleEffectView.setBackgroundColor(Color.argb(64, (int)(Math.random()*255),(int)(Math.random()*255), (int)(Math.random()*255)));
                        return false;
                    }
                } : null);
            }
        });


        ((RadioGroup) findViewById(R.id.typeGroup)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.checkedIdSoft:
                        rippleEffectView.setEdgeEffectType(RippleEffectView.RIPPLE_TYPE.SOFT);
                        break;
                    case R.id.checkedIdStrong:
                        rippleEffectView.setEdgeEffectType(RippleEffectView.RIPPLE_TYPE.STRONG);
                        break;
                }
            }
        });

        mSeekBarAlpha = (SeekBar) findViewById(R.id.alphaSeekBar);
        mSeekBarRed = (SeekBar) findViewById(R.id.redSeekBar);
        mSeekBarGreen = (SeekBar) findViewById(R.id.greenSeekBar);
        mSeekBarBlue = (SeekBar) findViewById(R.id.blueSeekBar);

        SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                rippleEffectView.setRippleColor(Color.argb(
                        mSeekBarAlpha.getProgress(),
                        mSeekBarRed.getProgress(),
                        mSeekBarGreen.getProgress(),
                        mSeekBarBlue.getProgress()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        };

        mSeekBarAlpha.setOnSeekBarChangeListener(seekBarChangeListener);
        mSeekBarRed.setOnSeekBarChangeListener(seekBarChangeListener);
        mSeekBarGreen.setOnSeekBarChangeListener(seekBarChangeListener);
        mSeekBarBlue.setOnSeekBarChangeListener(seekBarChangeListener);

       EditText text = (EditText)findViewById(R.id.clickDelayEditText);
        text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Integer value = Integer.getInteger(s.toString(), 0);
                if (value > 1000) value = 1000;
                rippleEffectView.setClickDelay(value);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ((SeekBar)findViewById(R.id.startAlphaSeekBar)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                rippleEffectView.setStartAlpha(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        ((SeekBar)findViewById(R.id.animationSeekBar)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                rippleEffectView.setAnimationDuration(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return 10;
        }

        @Override
        public String getItem(int position) {
            return "List item " + position;
        }

        @Override
        public long getItemId(int position) {

            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                View v = getLayoutInflater().inflate(R.layout.listitem, parent, false);
                convertView = RippleUtils.setRippleView(v);
            }
            RippleEffectViewContainer rippleEffectView = (RippleEffectViewContainer) convertView;
            ((TextView) rippleEffectView.findViewById(R.id.textView)).setText(getItem(position));
            rippleEffectView.getRippleView().clear();

            if (position % 2 == 0)
                rippleEffectView.getRippleView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("ripple", "click at" + position);
                        Toast.makeText(MainActivity.this, "click at" + position, Toast.LENGTH_SHORT).show();
                    }
                });
            if (position % 3 == 0)
                rippleEffectView.getRippleView().setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        Log.d("ripple", "long click at" + position);

                        Toast.makeText(MainActivity.this, "long click at" + position, Toast.LENGTH_SHORT).show();
                        return false;
                    }
                });

            return convertView;
        }
    }
}
