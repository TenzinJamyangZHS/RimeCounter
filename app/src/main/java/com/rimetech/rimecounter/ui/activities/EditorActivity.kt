package com.rimetech.rimecounter.ui.activities

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.ViewModelProvider
import com.rimetech.rimecounter.R
import com.rimetech.rimecounter.app.RimeCounter
import com.rimetech.rimecounter.data.Counter
import com.rimetech.rimecounter.databinding.ActivityEditorBinding
import com.rimetech.rimecounter.utils.COLOR_RED
import com.rimetech.rimecounter.utils.POS_TOP
import com.rimetech.rimecounter.utils.colorToRColorList
import com.rimetech.rimecounter.utils.colorToRIdList
import com.rimetech.rimecounter.utils.dpToPx
import com.rimetech.rimecounter.utils.getStatusBarHeight
import com.rimetech.rimecounter.utils.setBlur
import com.rimetech.rimecounter.utils.setMargin
import com.rimetech.rimecounter.utils.setPaintBackground
import com.rimetech.rimecounter.viewmodels.EditorViewModel
import com.rimetech.rimecounter.viewmodels.ListCounterViewModel
import com.rimetech.rimecounter.viewmodels.SettingsViewModel
import java.util.UUID

@Suppress("DEPRECATION", "INFERRED_TYPE_VARIABLE_INTO_POSSIBLE_EMPTY_INTERSECTION")
class EditorActivity : AppCompatActivity() {
    private val editorViewModel by lazy { ViewModelProvider(this)[EditorViewModel::class.java] }
    private val editorBinding by lazy { ActivityEditorBinding.inflate(layoutInflater) }
    private lateinit var settingsViewModel: SettingsViewModel
    private lateinit var listCounterViewModel: ListCounterViewModel
    private lateinit var fileLauncher: ActivityResultLauncher<Intent>
    private var theCounterId: UUID? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        supportActionBar?.hide()
        settingsViewModel = (application as RimeCounter).settingsViewModel
        listCounterViewModel = ViewModelProvider(this)[ListCounterViewModel::class.java]
        setContentView(editorBinding.root)
        editorBinding.lifecycleOwner = this
        editorBinding.settingsViewModel = settingsViewModel
        editorBinding.editorViewModel = editorViewModel

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.editor_root)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(0, 0, 0, systemBars.bottom)
            insets
        }

        setTopMargin()
        getTheCounterId()
        setBlurView()
        loadData()
        setLaunchers()
        setViewBackground()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        setTopMargin()
    }

    private fun setTopMargin() {
        editorBinding.blurView.setMargin(getStatusBarHeight(this), POS_TOP)
        editorBinding.nameLayout.setMargin(
            getStatusBarHeight(this) + this.dpToPx(90f).toInt(),
            POS_TOP
        )
    }

    private fun setBlurView() {
        editorViewModel.setCounterColorId(R.id.color_red)
        editorViewModel.setLabel("New Counter")
        editorViewModel.setActionText("Create")
        theCounterId?.let { counterId ->
            listCounterViewModel.getCounter(counterId).observe(this) { counter ->
                val counterColor = colorToRColorList.find { it.first==counter.color }?.second?:throw IllegalArgumentException("Counter color not found!")
                editorViewModel.setCounterColorId(editorViewModel.colorsMap.entries.find { it.value == counterColor }?.key?:throw IllegalArgumentException(
                    "Counter color not found in list!"
                ))
                editorViewModel.setLabel("Edit Counter")
                editorViewModel.setActionText("Edit")
            }

        }
        editorViewModel.label.observe(this) { lb ->
            editorBinding.label.text = lb
        }
        editorViewModel.actionText.observe(this) { at ->
            editorBinding.actionButton.text = at
        }

        settingsViewModel.isBlur.observe(this) { isBlur ->
            editorViewModel.counterColorId.observe(this) { colorId ->
                editorBinding.blurView.setPaintBackground(
                    if (isBlur) 200 else 255,
                    this.dpToPx(12f),
                    ContextCompat.getColor(
                        this,
                        editorViewModel.colorsMap[colorId] ?: R.color.color_red
                    )
                )
                window.navigationBarColor=ContextCompat.getColor(this,editorViewModel.colorsMap[colorId] ?: R.color.color_red)
                if (isBlur) {
                    editorBinding.blurView.setBlur(this, 18f)
                } else {
                    editorBinding.blurView.setBlurEnabled(false)
                }
            }
        }
    }


    private fun getTheCounterId() {
        theCounterId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("counter-id", UUID::class.java)
        } else {
            intent.getParcelableExtra("counter-id")
        }
    }

    private fun loadData() {
        theCounterId?.let { counterId ->
            listCounterViewModel.getCounter(counterId).observe(this) { counter ->
                editorViewModel.setName(counter.name)
                editorViewModel.setTheValue(counter.currentValue.toString())
                editorViewModel.setResetValue(counter.resetValue.toString())
                editorViewModel.setIncreaseValue(counter.increaseValue.toString())
                editorViewModel.setDecreaseValue(counter.decreaseValue.toString())
                editorViewModel.setAutoLength(counter.autoInSecond.toString())
                editorViewModel.setTargetValue(counter.targetValue.toString())
                editorViewModel.setTargetCircles(counter.targetCircle.toString())
                editorViewModel.setPerCircleSeconds(counter.targetSeconds.toString())
                counter.autoMediaUri?.let { uri ->
                    editorViewModel.setMediaUri(uri)
                    val file = DocumentFile.fromSingleUri(this, uri)
                    file?.let { fl ->
                        val name = fl.name
                        name?.let { nm ->
                            editorBinding.filePathText.text = nm
                        }
                    }
                }
            }

        }
    }

    private fun setLaunchers() {
        fileLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    result.data?.data?.let { uri ->
                        editorViewModel.setMediaUri(uri)
                        val file = DocumentFile.fromSingleUri(this, uri)
                        file?.let { fl ->
                            val name = fl.name
                            name?.let { nm ->
                                editorBinding.filePathText.text = nm
                            }
                        }
                        contentResolver.takePersistableUriPermission(
                            uri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                        )
                    }
                }
            }
    }

    private fun updateData() {
        if (theCounterId == null) {
            val newCounter = Counter(
                name = editorViewModel.name.value ?: "No Name Counter",
                color = colorToRIdList.find { it.second==editorViewModel.counterColorId.value }?.first?: COLOR_RED,
                currentValue = editorViewModel.theValue.value?.toIntOrNull() ?: 0,
                resetValue = editorViewModel.resetValue.value?.toIntOrNull() ?: 0,
                increaseValue = editorViewModel.increaseValue.value?.toIntOrNull() ?: 1,
                decreaseValue = editorViewModel.decreaseValue.value?.toIntOrNull() ?: 1,
                autoInSecond = editorViewModel.autoLength.value?.toIntOrNull() ?: 3,
                autoMediaUri = editorViewModel.mediaUri.value,
                targetValue = editorViewModel.targetValue.value?.toInt(),
                targetCircle = editorViewModel.targetCircles.value?.toInt(),
                targetSeconds = editorViewModel.perCircleSeconds.value?.toLong()
            )
            listCounterViewModel.addCounter(newCounter)
        } else {
            theCounterId?.let { id->
                listCounterViewModel.getCounter(id).observe(this){
                        counter->
                    counter.apply {
                        name = editorViewModel.name.value ?: "No Name Counter"
                        color = colorToRIdList.find { it.second==editorViewModel.counterColorId.value }?.first?: COLOR_RED
                        currentValue = editorViewModel.theValue.value?.toIntOrNull() ?: 0
                        resetValue = editorViewModel.resetValue.value?.toIntOrNull() ?: 0
                        increaseValue = editorViewModel.increaseValue.value?.toIntOrNull() ?: 1
                        decreaseValue = editorViewModel.decreaseValue.value?.toIntOrNull() ?: 1
                        autoInSecond = editorViewModel.autoLength.value?.toIntOrNull() ?: 3
                        autoMediaUri = editorViewModel.mediaUri.value
                        targetValue = editorViewModel.targetValue.value?.toInt()
                        targetCircle = editorViewModel.targetCircles.value?.toInt()
                        targetSeconds = editorViewModel.perCircleSeconds.value?.toLong()
                    }
                    listCounterViewModel.updateCounter(counter)
                }
            }
        }
    }

    private fun setViewBackground(){
        settingsViewModel.isMonet.observe(this){
                isMonet->
            val colorRoot = ContextCompat.getColor(this,if (isMonet)R.color.color_root_monet else R.color.color_root)
            editorBinding.editorRoot.setBackgroundColor(colorRoot)
            val colorLayer1 = ContextCompat.getColor(
                this,
                if (isMonet) R.color.color_layer1_monet else R.color.color_layer1
            )
            colorLayer1.let {
                val backgroundName = editorBinding.nameLayout.background
                if (backgroundName is GradientDrawable) {
                    backgroundName.setColor(it)
                }
                val backgroundColor = editorBinding.colorLayout.background
                if (backgroundColor is GradientDrawable) {
                    backgroundColor.setColor(it)
                }
                val backgroundValue = editorBinding.valueLayout.background
                if (backgroundValue is GradientDrawable) {
                    backgroundValue.setColor(it)
                }
                val backgroundFile = editorBinding.fileLayout.background
                if (backgroundFile is GradientDrawable) {
                    backgroundFile.setColor(it)
                }
            }
        }
    }


    fun onCounterColorChanged(view: View) {
        view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
        editorViewModel.setCounterColorId(view.id)
    }

    fun onChooseButtonClicked(view: View) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "audio/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        fileLauncher.launch(intent)
        view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
    }

    fun onActionButtonClicked(view: View) {
        updateData()
        view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
        finish()
    }
}