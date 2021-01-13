import React from 'react';
import ActionCable from 'actioncable';
import { API_WS_ROOT } from './constants';

export default class JavaIde extends React.Component {
  
  constructor(props) {
    super(props);
    console.log(this.props);

    this.state = {
      connectedToChannel: false,
      programOutput: [],
      connection: null,
      program: null,
      filename: null
    };
  }

  componentDidMount() {
    this.setUpCable();
  }

  onConnected = () => {
    console.log("connected to channel " + this.props.userId);
    this.setState({connectedToChannel: true});
  }

  onReceived = (data) => {
    console.log(data);
    const stdout = data.stdout
    // const stderr = data.stderr
    // const status = data.status
    let output = [stdout]
    if (data.stderr) {
      output.push(`Error: ${data.stderr}`)
    }
    this.setState({programOutput: [...this.state.programOutput, output]})
  }


  setUpCable = () => {
    const cable = ActionCable.createConsumer(API_WS_ROOT);
    const userId = this.props.userId;
    const onConnected = this.onConnected
    const onReceived = this.onReceived
    const connection = cable.subscriptions.create({channel: "JavaRunnerChannel", user_id: userId}, {
      connected() {
        // console.log("connected to channel " + userId);
        // setState({connectedToChannel: true});
        onConnected();
      },
  
      disconnected() {
        // Called when the subscription has been terminated by the server
      },
  
      received(data) {
        onReceived(data);
      },
  
      run_program(filename, text) {
        console.log("calling run program");
        this.perform("run_program", {filename: filename, text: text, user_id: userId})
      }
    });

    this.setState({connection: connection});
  }

  handleSubmit = (event) => {
    console.log(event);
    event.preventDefault();
    if (!this.state.filename || !this.state.program || !this.state.connection) {
      console.log("something is wrong...");
      return;
    }
    this.state.connection.run_program(this.state.filename, this.state.program);
  }

  handleInputChange = (event) => {
    const target = event.target;
    const value = target.value;
    const name = target.name;
    this.setState({
      [name]: value    
    });
  }

  render() {
    return (
      <div>
        <h1>Basic Java IDE</h1>
        <form onSubmit={this.handleSubmit}>
          <label>Your program
            <textarea rows={10} cols={80} name="program" onChange={this.handleInputChange}/>
          </label>
          <br/>
          <label>Filename
            <input type="text" name="filename" onChange={this.handleInputChange}/>
          </label>
          <input type="submit" value="Run My Program!"/>
        </form>
        <h2>Output</h2>
        <div>
          {this.state.programOutput.map((output) => (
            <p>{output}</p>
          ))}
        </div>
      </div>
    );
  }
}