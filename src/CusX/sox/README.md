# SOX project



## Included Components

* [React](https://reactjs.org/): A JavaScript library for building user interfaces.
* [TensorFlow.js](https://js.tensorflow.org/): A JavaScript library for training and deploying ML models in the browser and on Node.js.

## Featured Technologies

* Deep Learning: Subset of AI that uses
  multi-layers neural networks that learn from lots of data.
* Mobile: An environment to
 develop apps and enable engagements that are designed specifically for mobile
 users.
* Web Development: The construction of
  modern web apps using open-standards technologies.
* Visual Recognition: Tag, classify, and train
  visual content using machine learning.




### 1. Move to sox directory
```

Now go to the cloned repo directory:

```
cd sox
```


### 2. Install app dependencies

In the project directory, run:

```
yarn install
```


### 3. Download and convert pre-trained model

$ python -m venv myenv       # Python 3.X
$ virtualenv myenv           # Python 2.X

$ source myenv/bin/activate  # Mac or Linux
```

```bash
pip install tensorflowjs
```
```bash
python download_model.py
```
```bash
tensorflowjs_converter --input_format=keras ./mobilenet_1_0_224_tf.h5 ./my-model
```


### 4. Setup configuration files

```bash
mv ./my-model/* ./public/model/
```

#### Development Mode

In the project directory, run:

```
yarn start-dev
```

Runs the app in the development mode.<br />
Open [http://localhost:3000](http://localhost:3000) to view it in the browser.

```
node server.js
```

This will bring up the server which will serve both the API and built UI code.
Visit it at `http://localhost:5000`.




