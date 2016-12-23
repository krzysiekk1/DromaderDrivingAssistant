package com.skobbler.sdkdemo.fatigue;

import android.content.Context;
import android.content.res.AssetManager;

import com.skobbler.sdkdemo.R;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by marcinsendera on 24.11.2016.
 */

public class FuzzyLogicClass {

    // Filename with algorithm's fuzzy logic
    //TODO While debugging please check if the path is correct!!!!!!!!
    private static final String fatigue_filename = "/fatigue.fcl";

    // Names of variables
    private static final String executionTime = "execution";
    private static final String localTime = "local";

    // Name of output variable
    private static final String fatigue = "fatigue";

    // my fuzzy interface system
    private FIS fis;

    // Function Block
    private FunctionBlock fb;

    private Context context;



    public FuzzyLogicClass(Context context) throws FCLFileCannotBeOpenedException {


        this.context = context;
        //context.getResources().openRawResource(R.raw.fatigue);
        this.fis = FIS.load(context.getResources().openRawResource(R.raw.fatigue), true);

        if(fis == null) {

                System.err.println("Can't load .fcl to calculate fatigue - file: '" + this.fatigue_filename + "'");
                //System.exit(1);
        }

        // get default function block
        this.fb = this.fis.getFunctionBlock(null);

    }

    // TODO Change to return decision
    public double getValue(double localTime, double executionTime) {

        // Set inputs for 2 of our variables
        this.fb.setVariable(this.executionTime, executionTime);
        this.fb.setVariable(this.localTime, localTime);

        // evaluate with inuput variables
        this.fb.evaluate();

        // Show output variable's chart
        fb.getVariable(this.fatigue).defuzzify();

        // generation of output variable
        double output = fb.getVariable(this.fatigue).getValue();

        return output;
    }


    //TODO during next Sunday - Tuesday time: configure .fcl file (adding - rule block, terms shapes, defuzzify block) and adding communication with user/driver - showing
}
