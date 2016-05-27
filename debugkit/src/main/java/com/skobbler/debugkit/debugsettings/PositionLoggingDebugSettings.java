package com.skobbler.debugkit.debugsettings;

import android.os.Environment;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.skobbler.debugkit.R;
import com.skobbler.ngx.positioner.logging.SKPositionLoggingManager;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Tudor on 7/9/2015.
 */
public class PositionLoggingDebugSettings extends DebugSettings {

    private SKPositionLoggingManager.SPositionLoggingType fileType = SKPositionLoggingManager.SPositionLoggingType.SK_POSITION_LOGGING_TYPE_LOG;

    private String folderPath;

    private String currentPositionsFilePath;

    private boolean currentlyRecording;

    @Override
    List<Pair<String, Object>> defineKeyValuePairs() {
        folderPath = getLogFolderPath();
        List<Pair<String, Object>> pairs = new ArrayList<Pair<String, Object>>();
        pairs.add(new Pair<String, Object>(activity.getResources().getString(R.string.log_file_type), fileType.toString().replaceAll(".*_", "")));
        pairs.add(new Pair<String, Object>(activity.getResources().getString(R.string.log_file_path), folderPath));
        pairs.add(new Pair<String, Object>(activity.getResources().getString(R.string.start_logging_positions), null));
        pairs.add(new Pair<String, Object>(activity.getResources().getString(R.string.resume_logging_positions), null));
        pairs.add(new Pair<String, Object>(activity.getResources().getString(R.string.pause_logging_positions), null));
        pairs.add(new Pair<String, Object>(activity.getResources().getString(R.string.stop_logging_positions), null));
        return pairs;
    }

    @Override
    int defineSpecificLayout() {
        return R.layout.settings_position_logging;
    }

    @Override
    void onClose() {
        super.onClose();
        currentlyRecording = false;
        SKPositionLoggingManager.getInstance().stopLoggingPositions();
    }

    @Override
    void defineSpecificListeners() {

        specificLayout.findViewById(R.id.log_file_type).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!currentlyRecording) {
                    DebugSettings.getInstanceForType(PositionFileTypeDebugSettings.class).open(debugBaseLayout, PositionLoggingDebugSettings.this);
                } else {
                    Toast.makeText(activity, "File type can not be changed while recording is in progress", Toast.LENGTH_SHORT).show();
                }
            }
        });

        specificLayout.findViewById(R.id.start_logging).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentPositionsFilePath = folderPath + "/" + new SimpleDateFormat("yyyy-MM-dd_hh:mm:ss").format(new Date());
                currentlyRecording = true;
                SKPositionLoggingManager.getInstance().startLoggingPositions(currentPositionsFilePath, fileType);
                Toast.makeText(activity, "Started logging in " + getCurrentLogFileName(), Toast.LENGTH_SHORT).show();
            }
        });

        specificLayout.findViewById(R.id.resume_logging).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentlyRecording = true;
                SKPositionLoggingManager.getInstance().resumeLoggingPositions();
                Toast.makeText(activity, "Resumed logging in " + getCurrentLogFileName(), Toast.LENGTH_SHORT).show();
            }
        });

        specificLayout.findViewById(R.id.pause_logging).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentlyRecording = false;
                SKPositionLoggingManager.getInstance().pauseLoggingPositions();
                Toast.makeText(activity, "Paused logging in " + getCurrentLogFileName(), Toast.LENGTH_SHORT).show();
            }
        });

        specificLayout.findViewById(R.id.stop_logging).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentlyRecording = false;
                SKPositionLoggingManager.getInstance().stopLoggingPositions();
                Toast.makeText(activity, "Stopped logging in " + getCurrentLogFileName(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getCurrentLogFileName() {
        return currentPositionsFilePath + (fileType == SKPositionLoggingManager.SPositionLoggingType.SK_POSITION_LOGGING_TYPE_LOG ?
                ".log" : ".gpx");
    }

    private String getLogFolderPath() {
        String logStoragePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/logs";
        File folder = new File(logStoragePath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        return logStoragePath;
    }

    @Override
    void onChildClosed(DebugSettings closedChild) {
        super.onChildClosed(closedChild);
        if (closedChild instanceof PositionFileTypeDebugSettings) {
            fileType = SKPositionLoggingManager.SPositionLoggingType.values()[((PositionFileTypeDebugSettings) closedChild).getCurrentSelectedIndex()];
            ((TextView) specificLayout.findViewById(R.id.log_file_type).findViewById(R.id.property_value)).setText(fileType.toString().replaceAll(".*_", ""));
        }
    }
}
