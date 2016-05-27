package com.skobbler.debugkit.debugsettings;

import android.content.Context;
import android.util.Pair;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.skobbler.debugkit.R;
import com.skobbler.ngx.SKMaps;
import com.skobbler.ngx.map.SKAnnotation;
import com.skobbler.ngx.map.SKMapInternationalizationSettings;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mirceab on 18.06.2015.
 */
public class InternationalizationDebugSettings extends DebugSettings {
    /**
     * Internationalization language
     */
    SKMaps.SKLanguage skLanguage = SKMaps.SKLanguage.LANGUAGE_LOCAL;
    /**
     * Internationalization show both options
     */
    private boolean showOptions=false;
    /**
     * internationalization transliterate
     */
    private boolean showTransliterated=false;
    /**
     * Internationalization primary option
     **/
    SKMapInternationalizationSettings.SKMapInternationalizationOption skMapInternationalizationOption = SKMapInternationalizationSettings.SKMapInternationalizationOption.MAP_INTERNATIONALIZATION_OPTION_INTL;

    @Override
    List<Pair<String, Object>> defineKeyValuePairs() {
        List<Pair<String, Object>> keyValuePairs = new ArrayList<Pair<String, Object>>();
        Context context=specificLayout.getContext();
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.internationalizatio_title), null));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.internationalization_primary_option), skMapInternationalizationOption));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.internationalization_fallback_option), skMapInternationalizationOption));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.internationalization_primary_option), skLanguage));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.internationalization_fallback_option), skLanguage));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.internationalization_show_both_options), null));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.internationalization_transliterated), null));

        return keyValuePairs;
    }

    @Override
    int defineSpecificLayout() {
        return R.layout.internationalization_debug_kit;
    }

    @Override
    void defineSpecificListeners() {
        specificLayout.findViewById(R.id.primary_option_one).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DebugSettings.getInstanceForType(InternationalizationPrimaryOptionOne.class).open(debugBaseLayout, InternationalizationDebugSettings.this);
            }
        });
        specificLayout.findViewById(R.id.fallback_option_one).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DebugSettings.getInstanceForType(InternationalizationFallbackOptionOne.class).open(debugBaseLayout, InternationalizationDebugSettings.this);
            }
        });
        specificLayout.findViewById(R.id.primary_option_two).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DebugSettings.getInstanceForType(InternationalizationPrimaryOptionTwo.class).open(debugBaseLayout, InternationalizationDebugSettings.this);
            }
        });
        specificLayout.findViewById(R.id.fallback_option_two).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DebugSettings.getInstanceForType(InternationalizationFallbackOptionTwo.class).open(debugBaseLayout, InternationalizationDebugSettings.this);
            }
        });
        final View showBothOption = specificLayout.findViewById(R.id.show_both_options);
        showBothOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox internationalizationCheckBox = (CheckBox) showBothOption.findViewById(R.id.property_value);
                internationalizationCheckBox.setChecked(!internationalizationCheckBox.isChecked());
                if (internationalizationCheckBox.isChecked()) {
                    showOptions = true;
                }
                else {
                    showOptions=false;
                }
                showInternationalization();
            }
        });
        final View transliterate=specificLayout.findViewById(R.id.backup_to_transliterated);
        transliterate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox transliterateCheckBox= (CheckBox) transliterate.findViewById(R.id.property_value);
                transliterateCheckBox.setChecked(!transliterateCheckBox.isChecked());
                if (transliterateCheckBox.isChecked()) {
                    showTransliterated = true;
                }
                else {
                    showTransliterated=false;
                }
                showInternationalization();
            }
        });
    }

    @Override
    void onChildChanged(DebugSettings changedChild) {
        super.onChildChanged(changedChild);
        if (changedChild instanceof InternationalizationPrimaryOptionOne) {
            switch (((InternationalizationPrimaryOptionOne) changedChild).getCurrentSelectedIndex()) {
                case 0:
                    skMapInternationalizationOption= SKMapInternationalizationSettings.SKMapInternationalizationOption.MAP_INTERNATIONALIZATION_OPTION_NONE;
                    showInternationalization();
                    break;
                case 1:
                    skMapInternationalizationOption= SKMapInternationalizationSettings.SKMapInternationalizationOption.MAP_INTERNATIONALIZATION_OPTION_LOCAL;
                    showInternationalization();
                    break;
                case 2:
                    skMapInternationalizationOption= SKMapInternationalizationSettings.SKMapInternationalizationOption.MAP_INTERNATIONALIZATION_OPTION_TRANSLIT;
                    showInternationalization();
                    break;
                case 3:
                    skMapInternationalizationOption= SKMapInternationalizationSettings.SKMapInternationalizationOption.MAP_INTERNATIONALIZATION_OPTION_INTL;
                    showInternationalization();
                    break;
            }
        }
        if (changedChild instanceof InternationalizationFallbackOptionOne) {
            switch (((InternationalizationFallbackOptionOne) changedChild).getCurrentSelectedIndex()) {
                case 0:
                    skMapInternationalizationOption= SKMapInternationalizationSettings.SKMapInternationalizationOption.MAP_INTERNATIONALIZATION_OPTION_NONE;
                    showInternationalization();
                    break;
                case 1:
                    skMapInternationalizationOption= SKMapInternationalizationSettings.SKMapInternationalizationOption.MAP_INTERNATIONALIZATION_OPTION_LOCAL;
                    showInternationalization();
                    break;
                case 2:
                    skMapInternationalizationOption= SKMapInternationalizationSettings.SKMapInternationalizationOption.MAP_INTERNATIONALIZATION_OPTION_TRANSLIT;
                    showInternationalization();
                    break;
                case 3:
                    skMapInternationalizationOption= SKMapInternationalizationSettings.SKMapInternationalizationOption.MAP_INTERNATIONALIZATION_OPTION_INTL;
                    showInternationalization();
                    break;
            }
        }
        if (changedChild instanceof InternationalizationPrimaryOptionTwo) {
            switch (((InternationalizationPrimaryOptionTwo) changedChild).getCurrentSelectedIndex()) {
                case 0:
                    skLanguage= SKMaps.SKLanguage.LANGUAGE_LOCAL;
                    showInternationalization();
                    break;
                case 1:
                    skLanguage= SKMaps.SKLanguage.LANGUAGE_EN;
                    showInternationalization();
                    break;
                case 2:
                    skLanguage= SKMaps.SKLanguage.LANGUAGE_DE;
                    showInternationalization();
                    break;
                case 3:
                    skLanguage= SKMaps.SKLanguage.LANGUAGE_FR;
                    showInternationalization();
                    break;
                case 4:
                    skLanguage= SKMaps.SKLanguage.LANGUAGE_IT;
                    showInternationalization();
                    break;
                case 5:
                    skLanguage= SKMaps.SKLanguage.LANGUAGE_ES;
                    showInternationalization();
                    break;
                case 6:
                    skLanguage= SKMaps.SKLanguage.LANGUAGE_RU;
                    showInternationalization();
                    break;
                case 7:
                    skLanguage= SKMaps.SKLanguage.LANGUAGE_TR;
                    showInternationalization();
                    break;
            }
        }
        if (changedChild instanceof InternationalizationFallbackOptionTwo) {
            switch (((InternationalizationFallbackOptionTwo) changedChild).getCurrentSelectedIndex()) {
                case 0:
                    skLanguage= SKMaps.SKLanguage.LANGUAGE_LOCAL;
                    showInternationalization();
                    break;
                case 1:
                    skLanguage= SKMaps.SKLanguage.LANGUAGE_EN;
                    showInternationalization();
                    break;
                case 2:
                    skLanguage= SKMaps.SKLanguage.LANGUAGE_DE;
                    showInternationalization();
                    break;
                case 3:
                    skLanguage= SKMaps.SKLanguage.LANGUAGE_FR;
                    showInternationalization();
                    break;
                case 4:
                    skLanguage= SKMaps.SKLanguage.LANGUAGE_IT;
                    showInternationalization();
                    break;
                case 5:
                    skLanguage= SKMaps.SKLanguage.LANGUAGE_ES;
                    showInternationalization();
                    break;
                case 6:
                    skLanguage= SKMaps.SKLanguage.LANGUAGE_RU;
                    showInternationalization();
                    break;
                case 7:
                    skLanguage= SKMaps.SKLanguage.LANGUAGE_TR;
                    showInternationalization();
                    break;
            }
        }
    }
    private void showInternationalization(){
        SKMapInternationalizationSettings skMapInternationalizationSettings=new SKMapInternationalizationSettings();
        skMapInternationalizationSettings.setFirstLabelOption(skMapInternationalizationOption);
        skMapInternationalizationSettings.setSecondLabelOption(skMapInternationalizationOption);
        skMapInternationalizationSettings.setPrimaryLanguage(skLanguage);
        skMapInternationalizationSettings.setFallbackLanguage(skLanguage);
        skMapInternationalizationSettings.setShowBothLabels(showOptions);
        skMapInternationalizationSettings.setBackupTranslit(showTransliterated);
        activity.getMapView().getMapSettings().setMapInternationalizationSettings(skMapInternationalizationSettings);


    }
}